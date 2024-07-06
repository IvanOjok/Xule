package inc.pneuma.xule.widgets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class SpeechToTextParser(private val context:Context): RecognitionListener {

    private val _voiceState = MutableStateFlow(VoiceToTextParserState())
    val voiceState = _voiceState.asStateFlow()

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    fun startListening() {
        _voiceState.update { VoiceToTextParserState() }

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
            recognizer.setRecognitionListener(this)
            recognizer.startListening(intent)

            _voiceState.update {
                it.copy(
                    isSpeaking = true
                )
            }
        } else {
            _voiceState.update {
                it.copy(
                    error = "Recognizer not Available"
                )

            }
        }
    }

    fun stopListening() {

        _voiceState.update {
            it.copy(
                isSpeaking = false
            )
        }

        recognizer.stopListening()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _voiceState.update {
            it.copy(
                error = null
            )
        }
    }

    override fun onBeginningOfSpeech() = Unit

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        _voiceState.update {
            it.copy(
                isSpeaking = false
            )
        }
    }

    override fun onError(error: Int) {
        _voiceState.update {
            it.copy(
                error = "Error: $error"
            )
        }
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { result ->
            _voiceState.update {
                it.copy(
                    spokenText = result
                )
            }
        }
    }

    override fun onPartialResults(partialResults: Bundle?) = Unit

    override fun onEvent(eventType: Int, params: Bundle?) = Unit
}


data class VoiceToTextParserState(
    val isSpeaking: Boolean = false,
    val spokenText: String = "",
    val error: String? = null
)