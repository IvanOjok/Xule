package inc.pneuma.xule.ui.presentation

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import inc.pneuma.xule.widgets.XuleExposedDropdownMenuBox
import androidx.navigation.compose.rememberNavController
import inc.pneuma.xule.ui.vmodel.AuthViewModel
import inc.pneuma.xule.widgets.TextToSpeechParser

@Composable
fun XuleMainScreen(context: Context, name: String, isLoggedIn:Boolean = false, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavScreen.Home.route) {
        composable(NavScreen.Home.route) {
            SignUpScreen(context)
        }

        composable(NavScreen.Welcome.route) {
            LoggedInFirstScreen(name)
        }

        composable(NavScreen.Language.route) {
            LanguageSelectionScreen()
        }
    }

}

@Composable
fun LoggedInFirstScreen(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name, \n Welcome back. How are You today?",
        modifier = modifier
    )
}

/** composable handling audio or text selection */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(context: Context) {

    val information = "Welcome, I'm Xule; An AI Powered Learning Assistant. \n What mode of communication are You comfortable with? If You prefer text conversations only, say or select Text. If You prefer speech only, say Audio but if You would like both, say or select Both"
    val textToSpeechParser by lazy {
        TextToSpeechParser(context = context)
    }

    val textToSpeechState by textToSpeechParser.textState.collectAsState()
    
    textToSpeechParser.translateToSpeech(information)

    val vm by lazy {
        AuthViewModel()
    }
    val selectedMethod = rememberSaveable() { vm.selectedMethod }


    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick =
                    {
                        if(textToSpeechState.isSpeaking) textToSpeechParser.stopTranslator() else textToSpeechParser.translateToSpeech(information)
                    },
                ) {
                    AnimatedContent(targetState = textToSpeechState.isSpeaking, label = "volume") { isSpeaking ->
                        if (isSpeaking) {
                            Icon(imageVector = Icons.Rounded.VolumeOff, contentDescription = null)
                        } else {
                            Icon(imageVector = Icons.Rounded.VolumeUp, contentDescription = null)
                        }
                    }
                }
            }

        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(20.dp),
            contentAlignment = Alignment.Center) {
            Column {
                Text(
                    text = information,
                    modifier = Modifier,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(25.dp))
                XuleExposedDropdownMenuBox(
                    list = arrayOf("Text", "Audio", "Both"),
                    onElementSelected = {
                        /**save selected option to preferences via view model*/
                        vm._selectedMethod.value = it
                    }
                )

                Button(
                    onClick = {
                    /** get from vm and save to preferences for now */

                              }, modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(5.dp, 15.dp, 5.dp, 5.dp)) {
                    Text(text = "Continue", fontSize = 14.sp,)
                }
            }
        }
    }


}


/** composable handling language for communication */
@Composable
fun LanguageSelectionScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = "Thank You. What is Your preferred language of communication?",
                modifier = Modifier,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(25.dp))
            XuleExposedDropdownMenuBox(
                list = arrayOf("English", "French", "Spanish", "German"),
                onElementSelected = { /**save selected option to preferences via view model*/ })

            Button(onClick = { /** get from vm and save to preferences for now */ }, modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(5.dp, 15.dp, 5.dp, 5.dp)) {
                Text(text = "Continue", fontSize = 14.sp,)
            }
        }
    }
}

/** composable handling user name  */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputScreen() {
    var name by remember { mutableStateOf("") }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = "Thank You. I'm Xule, May I know Your name?",
                modifier = Modifier,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(25.dp))
            TextField(value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                label = {Text(text = "Enter Your Name")},
//                contentDescription = "username"
            )

            Button(onClick = { /** get from vm and save to preferences for now */ }, modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(5.dp, 15.dp, 5.dp, 5.dp)) {
                Text(text = "Continue", fontSize = 14.sp,)
            }
        }
    }
}

/** composable to handle gender  */
@Composable
fun GenderInputScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = "Thank You. May I know Your Gender so I can rightly address You? Be free to decline to my request",
                modifier = Modifier,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(25.dp))
            XuleExposedDropdownMenuBox(
                list = arrayOf("Male", "Female", "Lesbian", "Gay", "Bisexual", "Trans Woman", "Trans Man", "Queer", "Intersex", "Ally", "Rather not say"),
                onElementSelected = { /**save selected option to preferences via view model*/ })

            Button(onClick = { /** get from vm and save to preferences for now */ }, modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(5.dp, 15.dp, 5.dp, 5.dp)) {
                Text(text = "Continue", fontSize = 14.sp,)
            }
        }
    }
}

/** composable to handle avatar; show images based on gender, select from their icon or camera pic or  */


/** composable showing different features I can help with like assignments */



sealed class NavScreen(val route: String) {

    object Splash : NavScreen("Splash")

    object Welcome: NavScreen("Welcome")

    object Language: NavScreen("Language")

    object Home : NavScreen("Home")

    object PosterDetails : NavScreen("PosterDetails") {

        const val routeWithArgument: String = "PosterDetails/{posterId}"

        const val argument0: String = "posterId"
    }
}
