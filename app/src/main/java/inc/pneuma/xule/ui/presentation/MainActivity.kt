package inc.pneuma.xule.ui.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import inc.pneuma.xule.ui.theme.XuleTheme
import inc.pneuma.xule.ui.vmodel.AuthViewModel
import inc.pneuma.xule.widgets.SpeechToTextParser
import inc.pneuma.xule.widgets.TextToSpeechParser


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val speechToTextParser by lazy {
        SpeechToTextParser(this)
    }

    private val textToSpeechParser by lazy {
        TextToSpeechParser(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {
            XuleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    XuleMainScreen(this,"Android", true, activity = this)

                    var isConsent by remember { mutableStateOf(false) }
                    val permissions = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted -> isConsent = isGranted }
                    )
                    
                    LaunchedEffect(key1 = permissions, block = {
                        permissions.launch(Manifest.permission.RECORD_AUDIO)
                    })

                    val voiceState by speechToTextParser.voiceState.collectAsState()

                    val textToSpeechState by textToSpeechParser.textState.collectAsState()



//                    Scaffold() {
//
//
//                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun XulePreview() {
    XuleTheme {
        //XuleMainScreen(this, "Android")
        ///HomeScreen()
    }
}