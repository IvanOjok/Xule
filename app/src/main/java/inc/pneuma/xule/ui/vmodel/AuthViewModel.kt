package inc.pneuma.xule.ui.vmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import inc.pneuma.xule.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.asImageOrNull
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.mediapipe.framework.image.BitmapExtractor
import com.google.mediapipe.tasks.vision.imagegenerator.ImageGenerator
import com.google.mediapipe.tasks.vision.imagegenerator.ImageGenerator.ImageGeneratorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import inc.pneuma.xule.domain.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    //private val uriReader: UriReader
) : ViewModel() {

    val getTranslation =  mutableStateOf("")

    val getSelectedValueInMenu = mutableStateOf("")

    val selectedLanguage =  mutableStateOf("")
    //val selectedLanguage get() = _selectedLanguage.value

    val _userResponseParserState = mutableStateOf(UserResponseParserState())
    val userResponseParserState = _userResponseParserState.value


    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_KEY,
        safetySettings= listOf(
            SafetySetting(
              HarmCategory.SEXUALLY_EXPLICIT,
                BlockThreshold.NONE
            ),
            SafetySetting(
                HarmCategory.HARASSMENT,
                BlockThreshold.NONE
            ),
            SafetySetting(
                HarmCategory.HATE_SPEECH,
                BlockThreshold.NONE
            ),
            SafetySetting(
                HarmCategory.DANGEROUS_CONTENT,
                BlockThreshold.NONE
            )
        )
    )

    fun translateToLanguage(prompt: String, language:String)  {
        viewModelScope.launch {
            val response = generativeModel.generateContent(
                content {
                    text("Help translate $prompt in $language. Only translate, don't add any other information. ")
                }
            )
            getTranslation.value = response.text ?: "An error occurred"
        }
    }

    fun getUserResponse(prompt: String, reason: String) {
        Log.d("Request", prompt)
        viewModelScope.launch {
            delay(5000)
            if (prompt.isEmpty()) {
                delay(5000)
                return@launch
            } else {
                if (reason == "communication") {
                    Log.d("Request", prompt)
                    val response = generativeModel.generateContent(
                        content {
                            text("Does this mean text, audio, or both? Give me one word. $prompt")
                        }
                    )
                    Log.d("Response", "${response.text}")
                    if (response.text != null) {
                        preferenceRepository.saveSpeechOrText(response.text ?: "Both")
                    }
//                    userResponseParserState.copy(
//                        isLoading = false,
//                        response = response.text ?: "Both"
//                    )
                } else if (reason == "name") {
                    if (prompt.isNotEmpty()) {
                        preferenceRepository.saveUserName(prompt)
                    }
//                    userResponseParserState.copy(
//                        isLoading = false,
//                        response = prompt ?: ""
//                    )
                } else if(reason == "grade") {
                    if (prompt.isNotEmpty()) {
//                        val response = generativeModel.generateContent(
//                            content {
//                                text("$prompt; give me the age range for someone in this grade as a pair of two digits separated by a hyphen")
//                            }
//                        )
                        //if (response.text != null) {
                            preferenceRepository.saveUserGrade(prompt)
                        //}

                    }
                    userResponseParserState.copy(
                        isLoading = false,
                        response = prompt ?: ""
                    )
                }

            }
        }
    }

    var grade = mutableStateOf("")
    fun getGrade() {
        viewModelScope.launch {
            grade.value = preferenceRepository.getUserGrade().first()
        }
    }


    var subject = mutableStateOf("")
    var topic = mutableStateOf("")
    var question = mutableStateOf("")

    fun getLearnAndAssignmentQuestions(prompt: String, reason: String, context: Context) {
        viewModelScope.launch {
            if (reason == "subject") {
                preferenceRepository.saveSubjectQuestion(prompt)
                delay(1000)

                subject.value = preferenceRepository.getSubjectQuestion().first()

            } else if (reason == "topic") {
                preferenceRepository.saveSubjectTopic(prompt)
                delay(1000)

                topic.value = preferenceRepository.getSubjectTopic().first()

                /** Gemini work on question to ask then copy it in the response the prompt */
                    //ask gemini the question
                    val response = generativeModel.generateContent(
                        content {
                            if (!prompt.isNullOrEmpty()) text("Help me with one question about ${topic.value} in ${subject.value}. It can be multiple choice, require calculation or any other format but don't offer a solution")
                        }
                    )

                val gemQuestion = response.text ?: "An error occurred"
                preferenceRepository.saveExactQuestion(gemQuestion)
                delay(1000)

                question.value = preferenceRepository.getExactQuestion().first()
                sendChat(gemQuestion, null, "Xule", context)

            }
        }

    }

    fun getSpeechOrTextMode(): String {
        viewModelScope.launch {
           selectedLanguage.value = preferenceRepository.getSpeechOrText().first()
        }
        return selectedLanguage.value
    }


    val conversation: StateFlow<List<Chat>>
        get() = _conversation

    private val _conversation = MutableStateFlow(
        listOf(
            Chat(text = "Hi there, What subject would You like to be assessed in?",
            image = null,
            author = "Xule")
        )
    )

    fun sendChat(msg: String, img: List<Uri>?, author: String, context:Context) {

        // wrap the msg as ChatUiModel.Message and assign the author as 'me'
        val myChat = Chat(msg, img, author)
        viewModelScope.launch {

            // add myChat to the conversation
            _conversation.emit(_conversation.value + myChat)

            /** Gemini work on the prompt if question is set */
            val subject = preferenceRepository.getSubjectQuestion().first()
            val topic = preferenceRepository.getSubjectTopic().first()
            val question = preferenceRepository.getExactQuestion().first()
            if(!subject.isNullOrEmpty() && !topic.isNullOrEmpty() && !question.isNullOrEmpty() && author != "Xule" ) {
                //ask gemini the question
                val response = generativeModel.generateContent(
                    content {
                        if (!msg.isNullOrEmpty()) text("Help me assess if $msg is the right answer to $question")
                        if (!img.isNullOrEmpty()) {
                            img.forEach {
                                image(MediaStore.Images.Media.getBitmap(context.contentResolver, it))
                                text("Help me assess if the image above is the right answer to $question")
                            }
                        }
                    }
                )
                response.candidates.forEach {
                    it.content.parts.forEach {
                        it.asImageOrNull()
                    }
                }
                /** add to chat **/
                _conversation.emit(_conversation.value + Chat(response.text, null, "Xule"))

                //delete from preferences
                preferenceRepository.saveSubjectQuestion("")
                preferenceRepository.saveSubjectTopic("")
                preferenceRepository.saveExactQuestion("")

            }
        }
    }

    val learnConversation: StateFlow<List<Chat>>
        get() = _learnConversation

    private val _learnConversation = MutableStateFlow(
        listOf(
            Chat(text = "Hi there, What would You like to learn about today?",
                image = null,
                author = "Xule")
        )
    )

    fun sendLearnChat(msg: String, img: List<Uri>?, author: String, context: Context) {

        // wrap the msg as ChatUiModel.Message and assign the author as 'me'
        val myChat = Chat(msg, img, author)
        viewModelScope.launch {

            // add myChat to the conversation
            _learnConversation.emit(_learnConversation.value + myChat)

            /**get gemini response from here ***/

                //ask gemini the question
                val response = generativeModel.generateContent(
                    content {
                        if (!msg.isNullOrEmpty()) text("Help me teach me about this: $msg")
                        if(!img.isNullOrEmpty()) {
                            img.forEach {
                                image(MediaStore.Images.Media.getBitmap(context.contentResolver, it))
                                text("Help me teach me about the content in the image")
                            }
                        }
                    }
                )

            /** add response to chat **/
            _learnConversation.emit(_learnConversation.value + Chat(response.text, null, "Xule"))

            //response.text
        }
    }


    val readImage = mutableStateOf<Bitmap?>(null)
    val readText = mutableStateOf("")
    fun generateReadContent(context: Context) {
        viewModelScope.launch {
            val response = generativeModel.generateContent(
                content {
                    text("Help me with any random action that can be understood by a kid in kindergarten. Should be a single word for the action")
                }
            )

            readText.value = response.text ?: "run"

//            val opt = ImageGeneratorOptions.builder().setImageGeneratorModelDirectory("/data/local/tmp/image_generator/bins/").build()
//            val imageGenerator = ImageGenerator.createFromOptions(context, opt)
//            imageGenerator.setInputs("An image involving the action ${response.text }", 20,  0)
//
//            val result = imageGenerator.execute(true)
//
//            if (result?.generatedImage() != null) {
////                return Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888).apply {
////                    val canvas = Canvas(context)
////                    val paint = Paint()
////                    paint.color = Color.WHITE
////                    canvaas.drawPaint(paint)
////                }
//                val bitmap = BitmapExtractor.extract(result.generatedImage())
//                readImage.value = bitmap
//            }

//            response.candidates.forEach {
//                it.content.parts.forEach {
//                    if (it.asImageOrNull() != null) {
//                        readImage.value = it.asImageOrNull()
//                    }
//                    if (it.asTextOrNull() != null) {
//                        readText.value = it.asTextOrNull() ?: ""
//                    }
//                }
//            }
        }
    }

    //val readImage = mutableStateOf<Bitmap?>(null)
    val writeText = mutableStateOf("")
    val writeResponse = mutableStateOf("")
    fun generateWriteContent() {
        viewModelScope.launch {
            val response = generativeModel.generateContent(
                content {
                    text("Help me with one word that can be understood by a kid in kindergarten. It maybe a verb or a noun")
                }
            )

            writeText.value = response.text ?: ""
        }
    }

    val drawText = mutableStateOf("")
    fun generateDrawContent() {
        viewModelScope.launch {
            val response = generativeModel.generateContent(
                content {
                    Log.d("Generating", "Generating")
                    text("Help me with one word for any random object that can be drawn by a kid in kindergarten.")
                }
            )
            Log.d("Generated", "${response}")
            drawText.value = response.text ?: ""
        }
    }


    val shadeImage = mutableStateOf<Bitmap?>(null)
    val shadeText = mutableStateOf("")
    fun generateShadeContent(context: Context) {
        viewModelScope.launch {
            val response = generativeModel.generateContent(
                content {
                    text("Help me with a name of any random object that can be understood by a kid in kindergarten. Should a single word for the object")
                }
            )
            shadeText.value = response.text ?: "ball"

//            val opt = ImageGeneratorOptions.builder().setImageGeneratorModelDirectory("/data/local/tmp/image_generator/bins/").build()
//            val imageGenerator = ImageGenerator.createFromOptions(context, opt)
//            imageGenerator.setInputs("An image of ${response.text } without color and a transparent background", 20,  0)
//
//            val result = imageGenerator.execute(true)
//
//            if (result?.generatedImage() != null) {
////                return Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888).apply {
////                    val canvas = Canvas(context)
////                    val paint = Paint()
////                    paint.color = Color.WHITE
////                    canvaas.drawPaint(paint)
////                }
//                val bitmap = BitmapExtractor.extract(result.generatedImage())
//                shadeImage.value = bitmap
//            }

//            response.candidates.forEach {
//                it.content.parts.forEach {
//                    if (it.asImageOrNull() != null) {
//                        shadeImage.value = it.asImageOrNull()
//                    }
//                    if (it.asTextOrNull() != null) {
//                        shadeText.value = it.asTextOrNull() ?: ""
//                    }
//                }
//            }
        }
    }

    fun checkImageResemblance(msg: String, img: ImageBitmap) {
        viewModelScope.launch {
            val response = generativeModel.generateContent(
                content {
                    text("Does the content in this image look like $msg? Give me one word; Yes or No")
                    image(img.asAndroidBitmap())
                }
            )
            Log.d("Response", "${response.text}")

            writeResponse.value = response.text ?: "Yes"
        }
    }


    val storyImages = MutableStateFlow<List<Bitmap?>>(emptyList())
    //val storyText = MutableStateFlow<List<String?>>(emptyList())
    val storyText = mutableStateOf<String>("")
    fun generateStoryContent() {
        viewModelScope.launch {
            val response = generativeModel.generateContent(
                content {
                    text("Help me with a story that can be understood by a kid in kindergarten.")
                }
            )

            storyText.value = (
                response.text ?: ""
            )
//            val list = ArrayList<Bitmap?>()
//            val story = ArrayList<String>()
//            response.candidates.forEach {
//                it.content.parts.forEach {
//                    if (it.asImageOrNull() != null) {
//                        list.add(it.asImageOrNull())
//                        storyImages.emit(list)
//                    }
//                    if (it.asTextOrNull() != null) {
//                        story.add(it.asTextOrNull() ?: "")
//                        storyText.emit(story) //= it.asTextOrNull() ?: ""
//                    }
//                }
//            }
        }
    }
}

data class Chat(
    val text: String?,
    val image:List<Uri>?,
    val author: String,
) {

    companion object {
        val initConv = Chat(
            text = "Hi there, What subject would You like to be assessed in?",
            image = null,
            author = "Xule"
        )
    }
}

data class UserResponseParserState(
    val isLoading: Boolean = false,
    val response: String = "",
    val error: String? = null
)
