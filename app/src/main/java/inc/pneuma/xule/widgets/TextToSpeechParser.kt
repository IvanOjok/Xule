package inc.pneuma.xule.widgets

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale


class TextToSpeechParser(private val context:Context) {

    private val _textState = MutableStateFlow(TextToSpeechParserState())
    val textState = _textState.asStateFlow()

    private var translator: TextToSpeech ?= null

    fun translateToSpeech(speech:String) {
        translator = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                _textState.update {
                    it.copy(
                        isSpeaking = true
                    )
                }
                translator?.language = Locale.getDefault()
                translator?.setSpeechRate(1.0f)
                translator?.speak(
                    speech,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
                )

//                _textState.update {
//                    it.copy(
//                        isSpeaking = false
//                    )
//                }

            } else {
                translator?.stop()
                _textState.update {
                    it.copy(
                        isSpeaking = false,
                        error = "TextToSpeech Initialization Failed"
                    )
                }
            }
        }
    }


    fun stopTranslator() {
        translator?.stop()
        _textState.update {
            it.copy(
                isSpeaking = false,
            )
        }
    }
}

data class TextToSpeechParserState(
    val isSpeaking: Boolean = false,
    val spokenText: String = "",
    val error: String? = null
)

