package inc.pneuma.xule.ui.presentation

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.FindInPage
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import inc.pneuma.xule.widgets.XuleExposedDropdownMenuBox
import androidx.navigation.compose.rememberNavController
import inc.pneuma.xule.ui.theme.XuleTheme
import inc.pneuma.xule.ui.vmodel.AuthViewModel
import inc.pneuma.xule.widgets.SpeechToTextParser
import inc.pneuma.xule.widgets.TextToSpeechParser
import inc.pneuma.xule.widgets.UtteranceListener
import java.util.Locale

@Composable
fun XuleMainScreen(context: Context, name: String, isLoggedIn:Boolean = false, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val vm:AuthViewModel = hiltViewModel()
    val selectedCommunicationMethod = vm.getSpeechOrTextMode()

    NavHost(navController = navController, startDestination = NavScreen.Home.route) {
        composable(NavScreen.Home.route) {
            HomeScreen(context, navController, vm) //
        }

        composable(NavScreen.Sign.route) {
            SignUpScreen(context, navController, vm)
        }

        composable(NavScreen.Welcome.route) {
            LoggedInFirstScreen(name)
        }

        composable(NavScreen.Grade.route) {
            GradeSelectionScreen(context, navController, vm)
        }

        composable(NavScreen.NameInput.route) {
            NameInputScreen(context = context, navController = navController, communicationMethod = selectedCommunicationMethod, vm)
        }

        composable(NavScreen.Grade.route) {
            GradeSelectionScreen(context, navController, vm)
        }
        composable(NavScreen.Learn.route) {
            GradeSelectionScreen(context, navController, vm)
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
fun SignUpScreen(context: Context, navController: NavHostController, vm: AuthViewModel) {

    val myInformation = "Welcome, I'm Xule; An AI Powered Learning Assistant. \n What mode of communication are You comfortable with? If You prefer text conversations only, say or select Text. If You prefer speech only, say Audio but if You would like both, say or select Both"
    val textToSpeechParser by lazy { TextToSpeechParser(context = context) }
    val speechToTextParser by lazy { SpeechToTextParser(context) }

    val textToSpeechState by textToSpeechParser.textState.collectAsState()
    val speechParserState by speechToTextParser.voiceState.collectAsState()

    Log.d("Info", myInformation)
    //translate text into user's language
    var information = ""
    //vm.translateToLanguage(myInformation, "French")

    if (vm.getTranslation.value.isNotEmpty() && !vm.getTranslation.value.contains("error")) {
        information = vm.getTranslation.value
    }
    //create audio communication
    if (!textToSpeechState.isSpeaking) {
        textToSpeechComplete(myInformation, textToSpeechParser, speechToTextParser)
    }
//    textToSpeechParser.translateToSpeech(myInformation)
//        object: UtteranceListener {
//        override fun isSpeechCompleted() {
//            Log.d("Completed", "Voiced")
//            //speechToTextParser.startListening()
//        }
//    })



    //get preferred method of speech
    val selectedMethod =  remember { vm.getSelectedValueInMenu }


    if(speechParserState.isSpeaking) {
        vm.getUserResponse(speechParserState.spokenText ?: "", "communication")
    }

    if (vm.userResponseParserState.response.isNotEmpty()) {
        ///** get from vm and save to preferences for now */
        selectedMethod.value = vm.userResponseParserState.response
        navController.navigate(NavScreen.NameInput.route)
    }


    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick =
                    {
                        if(textToSpeechState.isSpeaking) textToSpeechParser.stopTranslator() else textToSpeechComplete(information, textToSpeechParser, speechToTextParser)
                    },
                ) {
                    AnimatedContent(targetState = textToSpeechState.isSpeaking, label = "volume") { isSpeaking ->
                        if (isSpeaking) {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.VolumeOff, contentDescription = null)
                        } else {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null)
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
                    text = myInformation,
                    modifier = Modifier,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(25.dp))
                XuleExposedDropdownMenuBox(
                    list = arrayOf("Text", "Audio", "Both"), vm
                )

                Button(
                    onClick = {
                    /** get from vm and save to preferences for now */
                        //save selectedMethod
                        Log.d("Methode, ", selectedMethod.value)

                        textToSpeechParser.stopTranslator()
                        navController.navigate(NavScreen.NameInput.route)
//                        if (selectedMethod.value.isNotEmpty()) {
//                            vm.getUserResponse(selectedMethod.value, "communication")
//                            navController.navigate(NavScreen.NameInput.route)
//                        }
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

fun textToSpeechComplete(information: String, textToSpeechParser: TextToSpeechParser, speechToTextParser: SpeechToTextParser) {
    textToSpeechParser.translateToSpeech(information, )
//    object: UtteranceListener {
//        override fun isSpeechCompleted() {
//            textToSpeechParser.stopTranslator()
//            speechToTextParser.startListening()
//        }
//    })
}



/** composable handling user name  */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputScreen(context: Context, navController: NavHostController, communicationMethod:String, vm: AuthViewModel = hiltViewModel()) {
    val myInformation = "Thank You. I'm Xule, May I know Your name?"
    val textToSpeechParser by lazy { TextToSpeechParser(context = context) }
    val speechToTextParser by lazy { SpeechToTextParser(context) }

    val textToSpeechState by textToSpeechParser.textState.collectAsState()
    val speechParserState by speechToTextParser.voiceState.collectAsState()

    Log.d("Info", myInformation)
    //translate text into user's language
    var information = ""
//    vm.translateToLanguage(myInformation, "French")

    if (vm.getTranslation.value.isNotEmpty() && !vm.getTranslation.value.contains("error")) {
        information = vm.getTranslation.value
    }



    //get name
    var name = rememberSaveable() { mutableStateOf("") }
    var isAudioMethod = false


    if (communicationMethod.isEmpty()) {
        navController.navigate(NavScreen.Home.route)
    } else if(communicationMethod.contains("audio")) {
        isAudioMethod = true
        //create audio communication
        //textToSpeechComplete(myInformation, textToSpeechParser, speechToTextParser)
    } else {
        isAudioMethod = false
    }

    //vm.translateToLanguage(myInformation, Locale.getDefault().displayLanguage)

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = information,
                modifier = Modifier,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(25.dp))
            TextField(value = name.value,
                onValueChange = { name.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                label = {Text(text = "Enter Your Name")},
            )

            Button(onClick = { /** get from vm and save to preferences for now */
                             vm.getUserResponse(name.value, "name")
                navController.navigate(NavScreen.Grade.route)
                             }, modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(5.dp, 15.dp, 5.dp, 5.dp)) {
                Text(text = "Continue", fontSize = 14.sp,)
            }
        }
    }

}


/** composable handling grades */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeSelectionScreen(context: Context, navController: NavHostController, vm: AuthViewModel) {

    val myInformation = "Nice to meet You Grace. In what school grade are You?"
    val textToSpeechParser by lazy {
        TextToSpeechParser(context = context)
    }
    val speechToTextParser by lazy { SpeechToTextParser(context) }

    val selectedLanguage = rememberSaveable() { vm.selectedLanguage }

    val textToSpeechState by textToSpeechParser.textState.collectAsState()
    val speechParserState by speechToTextParser.voiceState.collectAsState()



    //translate text into user's language
    var information = ""
//    vm.translateToLanguage(myInformation, "French")

    if (vm.getTranslation.value.isNotEmpty() && !vm.getTranslation.value.contains("error")) {
        information = vm.getTranslation.value
    }
    //create audio communication
    textToSpeechComplete(myInformation, textToSpeechParser, speechToTextParser)


    //get preferred method of speech
    val grade = rememberSaveable() { vm.getSelectedValueInMenu }


    if(speechParserState.isSpeaking) {
        vm.getUserResponse(speechParserState.spokenText ?: "", "communication")
    }



    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
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
                list = arrayOf("Kindergarten", "Primary (Grade 1- Grade 5)", "Junior High (Grade 6- Grade 7)", "High School (Grade 8- Grade 12)"), vm
            )

            Button(onClick = {
                /** get from vm and save to preferences for now */
                if (selectedLanguage.value.isNotEmpty()) {
                    vm.getUserResponse(grade.value, "grade")
                    navController.navigate(NavScreen.Welcome.route)
                }
            }, modifier = Modifier
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
//            XuleExposedDropdownMenuBox(
//                list = arrayOf("Male", "Female", "Lesbian", "Gay", "Bisexual", "Trans Woman", "Trans Man", "Queer", "Intersex", "Ally", "Rather not say"),
//                onElementSelected = { /**save selected option to preferences via view model*/ })

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(context: Context, navController: NavHostController, vm: AuthViewModel) { //

    Scaffold(
        floatingActionButton = {

        }
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(10.dp),
            //contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.TopCenter) {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), colors = CardDefaults.cardColors(containerColor = Color.Cyan)) {

                        Column(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.End) {
                            Icon(imageVector = Icons.Rounded.Settings, contentDescription = null, modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(top = 50.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Rounded.PersonPinCircle, contentDescription = null, modifier = Modifier
                            .height(150.dp)
                            .width(150.dp)
                            )
                        Text(text = "Hello Peter")
                    }

                }

                Row(modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()) {
                    Card(onClick = {
                        navController.navigate(NavScreen.Learn.route)
                    }, modifier = Modifier.weight(1F)) {
                        Column(modifier = Modifier.width(150.dp).height(150.dp).padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Rounded.School, contentDescription = null, modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Learn")
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Card {
                        Column(modifier = Modifier.width(150.dp).height(150.dp).padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.Assignment, contentDescription = null, modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Assess")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Card {
                        Column(modifier = Modifier.width(150.dp).height(150.dp).weight(1F).padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Rounded.FindInPage, contentDescription = null, modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Proof Read")
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Card {
                        Column(modifier = Modifier.width(150.dp).height(150.dp).padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.AutoMirrored.Rounded.Chat, contentDescription = null, modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Chat")
                        }
                    }
                }

            }

        }
    }
    
}





sealed class NavScreen(val route: String) {

    object Splash : NavScreen("Splash")

    object Sign: NavScreen("Sign")

    object Welcome: NavScreen("Welcome")

    object Grade: NavScreen("Grade")

    object Home : NavScreen("Home")

    object NameInput: NavScreen("Name")

    object Learn: NavScreen("learn")

    object PosterDetails : NavScreen("PosterDetails") {

        const val routeWithArgument: String = "PosterDetails/{posterId}"

        const val argument0: String = "posterId"
    }
}
