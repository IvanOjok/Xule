package inc.pneuma.xule.ui.vmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import inc.pneuma.xule.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import inc.pneuma.xule.domain.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    val getTranslation =  mutableStateOf("")

    val getSelectedValueInMenu = mutableStateOf("")

    val selectedLanguage =  mutableStateOf("")
    //val selectedLanguage get() = _selectedLanguage.value

    val _userResponseParserState = mutableStateOf(UserResponseParserState())
    val userResponseParserState = _userResponseParserState.value


    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_KEY
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

    fun getSpeechOrTextMode(): String {
        viewModelScope.launch {
           selectedLanguage.value = preferenceRepository.getSpeechOrText().first()
        }
        return selectedLanguage.value
    }





}

data class UserResponseParserState(
    val isLoading: Boolean = false,
    val response: String = "",
    val error: String? = null
)