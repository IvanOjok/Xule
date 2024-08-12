package inc.pneuma.xule.ui.presentation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.FilePresent
import androidx.compose.material.icons.rounded.FindInPage
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import inc.pneuma.xule.widgets.XuleExposedDropdownMenuBox
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import inc.pneuma.xule.ui.theme.XuleTheme
import inc.pneuma.xule.ui.vmodel.AuthViewModel
import inc.pneuma.xule.widgets.SpeechToTextParser
import inc.pneuma.xule.widgets.TextToSpeechParser
import inc.pneuma.xule.widgets.UtteranceListener
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@Composable
fun XuleMainScreen(context: Context, name: String, isLoggedIn:Boolean = false, modifier: Modifier = Modifier, activity: Activity) {
    val navController = rememberNavController()
    val vm:AuthViewModel = hiltViewModel()
    val selectedCommunicationMethod = vm.getSpeechOrTextMode()
    vm.getGrade()

    val grade = vm.grade.value
    val subject = vm.subject.value
    val topic = vm.topic.value
    val questions = vm.question.value


    NavHost(navController = navController, startDestination = NavScreen.Grade.route) {
        composable(NavScreen.Home.route) {
            HomeScreen(context, navController, vm, grade) //
        }

        composable(NavScreen.Sign.route) {
            SignUpScreen(context, navController, vm)
        }

        composable(NavScreen.Welcome.route) {
            LoggedInFirstScreen(name)
        }

        composable(NavScreen.NameInput.route) {
            NameInputScreen(context = context, navController = navController, communicationMethod = selectedCommunicationMethod, vm)
        }

        composable(NavScreen.Grade.route) {
            GradeSelectionScreen(context, navController, vm)
        }
        composable(NavScreen.Learn.route) {
            LearnScreen(navController, vm, activity)
        }
        composable(NavScreen.Scanner.route) {
            ScannerScreen(context, activity)
        }

        composable(NavScreen.Assess.route) {
            AssignmentScreen(navController, subject = subject, topic = topic, question = questions, vm, activity)
        }

        composable(NavScreen.Shade.route) {
            //ShadeScreen()
        }

        composable(NavScreen.ChildRead.route) {
            ChildReadScreen(context = context, navController = navController, vm = vm)
        }

        composable(NavScreen.ChildWrite.route) {
            ChildWriteScreen(context = context, navController = navController, vm = vm)
        }

        composable(NavScreen.ChildStory.route) {
            ChildStoryScreen(context = context, navController = navController, vm = vm)
        }

        composable(NavScreen.ChildDraw.route) {
            ChildDrawScreen(context = context, navController = navController, vm = vm)
        }

        composable(NavScreen.ChildShade.route) {
            ChildShadeScreen(context = context, navController = navController, vm = vm)
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

    val myInformation = "In what school grade are You?"
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
                text = myInformation,
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
                    navController.navigate(NavScreen.Home.route)
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
fun HomeScreen(context: Context, navController: NavHostController, vm: AuthViewModel, grade:String) { //

    Scaffold(
        floatingActionButton = {

        }
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            //.padding(10.dp),
            //contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.TopCenter) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(color = Color(0xFF4CAF50))) {

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.End) {
                            Icon(imageVector = Icons.Rounded.Settings, contentDescription = null, modifier = Modifier
                                .height(45.dp)
                                .width(45.dp).clickable {
                                    navController.navigate(NavScreen.Grade.route)
                                }
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(top = 70.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = null, modifier = Modifier
                            .height(120.dp)
                            .width(120.dp)
                            )
                        Text(text = "Hello Peter")
                    }

                }

                if (!grade.contains("Kindergarten")) {
                    Row(modifier = Modifier
                        .padding(10.dp)
                        .height(200.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Card(onClick = {
                            navController.navigate(NavScreen.Learn.route)
                        }, modifier = Modifier.align(CenterVertically), ) {
                            Column(modifier = Modifier
                                .height(120.dp)
                                .width(120.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.School, contentDescription = null, modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Learn")
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Card(onClick = {
                            navController.navigate(NavScreen.Assess.route)
                        }, modifier = Modifier.align(CenterVertically)) {
                            Column(modifier = Modifier
                                .height(120.dp)
                                .width(120.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
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
                        .height(200.dp)
                        .padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Card {
                            Column(modifier = Modifier
                                .height(120.dp)
                                .width(120.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
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
                            Column(modifier = Modifier
                                .height(150.dp)
                                .width(120.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.AutoMirrored.Rounded.Chat, contentDescription = null, modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Chat")
                            }
                        }
                    }
                } else {
                    Row(modifier = Modifier
                        .padding(10.dp)
                        .height(200.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Card(onClick = {
                            navController.navigate(NavScreen.ChildRead.route)
                        }, modifier = Modifier.align(CenterVertically), ) {
                            Column(modifier = Modifier
                                .width(120.dp)
                                .height(150.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.MenuBook, contentDescription = null, modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Read")
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Card(onClick = {
                            navController.navigate(NavScreen.ChildWrite.route)
                        }, modifier = Modifier.align(CenterVertically)) {
                            Column(modifier = Modifier
                                .width(120.dp)
                                .height(150.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.EditNote, contentDescription = null, modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Write")
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Card(onClick = {
                            navController.navigate(NavScreen.ChildStory.route)
                        }, modifier = Modifier.align(CenterVertically)) {
                            Column(modifier = Modifier
                                .width(120.dp)
                                .height(150.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.AutoStories, contentDescription = null, modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Story")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Card(onClick = {
                            navController.navigate(NavScreen.ChildDraw.route)
                        }, modifier = Modifier.align(CenterVertically)) {
                            Column(modifier = Modifier
                                .width(120.dp)
                                .height(150.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.Draw, contentDescription = null, modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Draw")
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Card(onClick = {
                            navController.navigate(NavScreen.ChildShade.route)
                        }, modifier = Modifier.align(CenterVertically)) {
                            Column(modifier = Modifier
                                .width(120.dp)
                                .height(150.dp)
                                .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.Brush, contentDescription = null, modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Shade")
                            }
                        }
                    }
                }


            }

        }
    }
    
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable fun LearnScreen(navController: NavHostController, vm: AuthViewModel, x:Activity) {

    var input by remember {
        mutableStateOf("")
    }
    var imageUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clickable {
                        navController.navigateUp()
                    } )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Learn")
            }

        }) },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.AddCircleOutline, contentDescription = null, modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clickable {
                        //navController.navigate(NavScreen.Scanner.route)
                        showBottomSheet = !showBottomSheet

                    })
                TextField(value = input, onValueChange = { input = it }, placeholder = {
                    Text(text = "Enter Your Response here...")
                }, trailingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {

                    }
                })
                Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clickable {
                        if (input.isNotEmpty()) {
                            vm.sendLearnChat(input, null, "You", x)

                            vm.getLearnAndAssignmentQuestions(input, "learn", x)
                        }
                        input = ""
                    })
            }
        }

    ) { padding ->

        val galleryLauncher = rememberLauncherForActivityResult(
            //contract = ActivityResultContracts.PickVisualMedia(),
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uri ->
                uri.let {
                    imageUris = it

                    vm.sendLearnChat("", imageUris, "You", x)
                }
            }
        )

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = {

            })


        //Document scanner options
        val options = GmsDocumentScannerOptions.Builder().setScannerMode(SCANNER_MODE_FULL).setGalleryImportAllowed(true).setPageLimit(3)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .build()
        val scanner = GmsDocumentScanning.getClient(options)

        val activity = x as MainActivity
        val scannerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                imageUris = result?.pages?.map { it.imageUri  } ?: emptyList()
                vm.sendLearnChat("", imageUris, "You",x)

//                result?.pdf?.let { pdf->
//                    val fos = FileOutputStream(File(activity.filesDir, "scanned.pdf"))
//                    activity.contentResolver.openInputStream(pdf.uri)?.use {
//                        it
//                    }
//
//                    //vm.sendChat("Images", pdf.uri, "You")
//                }

            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column {
                        Icon(imageVector = Icons.Rounded.DocumentScanner, contentDescription = null, modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable {
//                                navController.navigate(NavScreen.Scanner.route)
                                scanner
                                    .getStartScanIntent(activity)
                                    .addOnSuccessListener {
                                        scannerLauncher.launch(
                                            IntentSenderRequest
                                                .Builder(it)
                                                .build()
                                        )
                                    }
                                    .addOnFailureListener {
                                        Toast
                                            .makeText(x, it.message, Toast.LENGTH_LONG)
                                            .show()
                                    }
                            } )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Scan")
                    }
                    Spacer(modifier= Modifier.width(5.dp))
                    Column {
                        Icon(imageVector = Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable {

                            } )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Camera")
                    }
                    Spacer(modifier= Modifier.width(5.dp))
                    Column {
                        Icon(imageVector = Icons.Rounded.FilePresent, contentDescription = null, modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            } )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Pictures")
                    }
                }
//                Button(onClick = {
//                    scope.launch { sheetState.hide() }.invokeOnCompletion {
//                        if (!sheetState.isVisible) {
//                            showBottomSheet = false
//                        }
//                    }
//                }) {
//                    Text("Hide bottom sheet")
//                }
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            LazyColumn() {
                items(vm.learnConversation.value) {item ->
//                    Box(
//                        modifier = Modifier
//                            //.align(if (item.author != "Xule") End else Start)
//                            .clip(
//                                RoundedCornerShape(
//                                    topStart = 48f,
//                                    topEnd = 48f,
//                                    bottomStart = if (item.author == "Xule") 48f else 0f,
//                                    bottomEnd = if (item.author != "Xule") 0f else 48f
//                                )
//                            )
//                            .background(MaterialTheme.colorScheme.tertiary)
//                            .padding(16.dp,),
//                        //contentAlignment = if (item.author != "Xule") End else End
//                    ) {
                    val paddingStart = if (item.author == "Xule") 5.dp else 100.dp
                    val paddingEnd = if (item.author == "Xule") 100.dp else 5.dp
                    val background = if (item.author == "Xule") MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(48f))
                        .padding(start = paddingStart, end = paddingEnd, bottom = 5.dp)
                        .background(background), horizontalAlignment = if (item.author != "Xule") Alignment.End else Alignment.Start) {
                            if (!item.text.isNullOrEmpty()) Text(text = item.text)
                            if (!item.image.isNullOrEmpty()) {
                                //LazyColumn() {
                                    //items(item.image) {
                                        AsyncImage(model = item.image[0], contentDescription = null, contentScale = ContentScale.FillBounds, modifier = Modifier
                                            .fillMaxWidth()
                                            .height(260.dp))
                                    //}
                                //}
                            }

                        }

                    //}
                }
            }

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AssignmentScreen(navController: NavHostController, subject:String?, topic:String?, question: String?, vm: AuthViewModel, x:Activity) {

    var input by remember {
        mutableStateOf("")
    }
    var imageUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showShadeScreen by remember { mutableStateOf(false) }

    //val image = remember { drawToImage() }

    Scaffold(
        topBar = { TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clickable {
                        navController.navigateUp()
                    } )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Assignment")
            }

        }) },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.AddCircleOutline, contentDescription = null, modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clickable {
                        //navController.navigate(NavScreen.Scanner.route)
                        showBottomSheet = !showBottomSheet

                    })
                TextField(value = input, onValueChange = { input = it }, placeholder = {
                    Text(text = "Enter Your Response here...")
                }, trailingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {

                    }
                })
                Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clickable {
                        if (input.isNotEmpty()) {
                            vm.sendChat(input, null, "You", x)
                            if (vm.subject.value.isNullOrEmpty()) {
                                vm.getLearnAndAssignmentQuestions(input, "subject", x)
                            } else if (vm.subject.value.isNotEmpty() && vm.topic.value.isNullOrEmpty()) {
                                vm.getLearnAndAssignmentQuestions(input, "topic", x)
                            }
                            input = ""
//                            else if(vm.subject.value.isNotEmpty() && vm.topic.value.isNotEmpty() && vm.question.value.isNullOrEmpty()) {
//                                vm.getLearnAndAssignmentQuestions(input, "question")
//                            } else if(vm.subject.value.isNotEmpty() && vm.topic.value.isNotEmpty() && vm.question.value.isNotEmpty()) {
//                                vm.getLearnAndAssignmentQuestions(input, "assess")
//                            }
                        }
                    })
            }
        }

    ) { padding ->

        val galleryLauncher = rememberLauncherForActivityResult(
            //contract = ActivityResultContracts.PickVisualMedia(),
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uri ->
                uri.let {
                    imageUris = it

                    vm.sendChat("Images", imageUris, "You", x)
                }
            }
        )

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = {

            })


        //Document scanner options
        val options = GmsDocumentScannerOptions.Builder().setScannerMode(SCANNER_MODE_FULL).setGalleryImportAllowed(true).setPageLimit(5)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .build()
        val scanner = GmsDocumentScanning.getClient(options)

        val activity = x as MainActivity
        val scannerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                imageUris = result?.pages?.map { it.imageUri  } ?: emptyList()
                vm.sendChat("Images", imageUris, "You", x)

//                result?.pdf?.let { pdf->
//                    val fos = FileOutputStream(File(activity.filesDir, "scanned.pdf"))
//                    activity.contentResolver.openInputStream(pdf.uri)?.use {
//                        it
//                    }
//
//                    //vm.sendChat("Images", pdf.uri, "You")
//                }

            }
        }

//        if (showShadeScreen) {
//            ShadeScreen()
//        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column {
                        Icon(imageVector = Icons.Rounded.DocumentScanner, contentDescription = null, modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable {
//                                navController.navigate(NavScreen.Scanner.route)
                                scanner
                                    .getStartScanIntent(activity)
                                    .addOnSuccessListener {
                                        scannerLauncher.launch(
                                            IntentSenderRequest
                                                .Builder(it)
                                                .build()
                                        )
                                    }
                                    .addOnFailureListener {
                                        Toast
                                            .makeText(x, it.message, Toast.LENGTH_LONG)
                                            .show()
                                    }
                            } )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Scan")
                    }
                    Spacer(modifier= Modifier.width(5.dp))
                    Column {
                        Icon(imageVector = Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable {

                            } )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Camera")
                    }
                    Spacer(modifier= Modifier.width(5.dp))
                    Column {
                        Icon(imageVector = Icons.Rounded.FilePresent, contentDescription = null, modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            } )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "Pictures")
                    }
//                    Spacer(modifier= Modifier.width(5.dp))
//                    Column {
//                        Icon(imageVector = Icons.Rounded.Draw, contentDescription = null, modifier = Modifier
//                            .width(50.dp)
//                            .height(50.dp)
//                            .clickable {
//                                showShadeScreen = true
//                            } )
//                        Spacer(modifier = Modifier.height(5.dp))
//                        Text(text = "Free")
//                    }
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            if(vm.subject.value.isNotEmpty() && vm.topic.value.isNullOrEmpty()) {
                vm.sendChat(msg = "What topic would You like to be assessed in?", img = null, author="Xule", x)
            }


            LazyColumn() {
                items(vm.conversation.value) {item ->
                        val paddingStart = if (item.author == "Xule") 5.dp else 100.dp
                        val paddingEnd = if (item.author == "Xule") 100.dp else 5.dp
                        val background = if (item.author == "Xule") MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary
                        val bottomStart = if(item.author == "Xule") 0f else 48f
                        val bottomEnd = if(item.author == "Xule") 48f else 0f

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(48f, 48f, bottomEnd, bottomStart))
                            .padding(start = paddingStart, end = paddingEnd, bottom = 5.dp)
                            .background(background), horizontalAlignment = if (item.author != "Xule") Alignment.End else Alignment.Start) {
                        //Column(horizontalAlignment = if (item.author != "Xule") Alignment.End else Alignment.Start) {
                            if (!item.text.isNullOrEmpty()) Text(text = item.text)
                            if (!item.image.isNullOrEmpty()) {
                                //LazyColumn() {
                                    //items(item.image) {
                                        AsyncImage(model = item.image[0], contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier
                                            .width(150.dp)
                                            .height(100.dp))
                                    //}
                                //}
                            //}

                        }

                    }
                }
            }

        }
    }
}


@Composable fun ScannerScreen(context:Context, x: Activity) {
    val options = GmsDocumentScannerOptions.Builder().setScannerMode(SCANNER_MODE_FULL).setGalleryImportAllowed(true).setPageLimit(5)
        .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
        .build()
    val scanner = GmsDocumentScanning.getClient(options)

    var imageUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    val activity = x as MainActivity
    val scannerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
            imageUris = result?.pages?.map { it.imageUri  } ?: emptyList()

            result?.pdf?.let { pdf->
                val fos = FileOutputStream(File(activity.filesDir, "scanned.pdf"))
                activity.contentResolver.openInputStream(pdf.uri)?.use {
                    it
                }
            }
        }
    }

    scanner.getStartScanIntent(activity).addOnSuccessListener {
        scannerLauncher.launch (
            IntentSenderRequest.Builder(it).build()
        )
    }.addOnFailureListener {
        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        imageUris.forEach { uri ->
            AsyncImage(model = uri, contentDescription = null, contentScale = ContentScale.Fit)
        }
//        Button(onClick = {
//            scanner.getStartScanIntent(activity).addOnSuccessListener {
//                scannerLauncher.launch (
//                    IntentSenderRequest.Builder(it).build()
//                    )
//            }.addOnFailureListener {
//                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
//            }
//        }) {
//            Text(text = "Scan")
//        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ShadeScreen(context: Context, image: ImageBitmap) {

    val lines = remember { mutableStateListOf<Line>() }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null, modifier=Modifier.clickable {
//                    val bitmap = drawToImage().asAndroidBitmap()
//
//                    val bs = ByteArrayOutputStream()
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs)
//                    val uriString = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "M", null)
//                    val uri = Uri.parse(uriString)

                })
            }
        }

    ) { padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawImage(image)
            }

//            Canvas(modifier = Modifier
//                .fillMaxSize()
//                .pointerInput(true) {
//                    detectDragGestures { change, dragAmount ->
//                        change.consume()
//
//                        val line = Line(
//                            start = change.position - dragAmount,
//                            end = change.position
//                        )
//                        lines.add(line)
//                    }
//                }) {
//                lines.forEach { line ->
//                    drawLine(
//                        color = line.color,
//                        start = line.start,
//                        end = line.end,
//                        strokeWidth = line.strokeWidth.toPx(),
//                        cap = StrokeCap.Round
//                    )
//                }
//            }

        }
    }
}

@Composable
fun DrawToImage(color: Color = Color.Black, width: Float = 1f, orgBitmap: ImageBitmap?= null): ImageBitmap {
    val lines = remember { mutableStateListOf<Line>() }
    val drawScope = CanvasDrawScope()
    val size = Size(400f, 400f)
    val bitmap = if (orgBitmap == null) ImageBitmap(size.width.toInt(), size.height.toInt()) else orgBitmap
    val canvas = androidx.compose.ui.graphics.Canvas(bitmap)

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(true) {
            detectDragGestures { change, dragAmount ->
                change.consume()

                val line = Line(
                    start = change.position - dragAmount,
                    end = change.position
                )
                lines.add(line)
            }
        }){
        this.draw(density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = size) {
            lines.forEach { line ->
                    drawLine(
                        color = line.color,
                        start = line.start,
                        end = line.end,
                        strokeWidth = line.strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                }
        }
    }
//    drawScope.draw(
//        density = Density(1f),
//        layoutDirection = LayoutDirection.Ltr,
//        canvas = canvas,
//        size = size
//    ) {
//        drawRect(color = Color.White, topLeft = Offset.Zero, size=size)
//        drawLine(
//            color = color,
//            start = Offset.Zero,
//            end = Offset(size.width, size.height),
//            strokeWidth = width,
//            cap = StrokeCap.Round
//        )
//    }
    return bitmap
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildReadScreen(context: Context, navController: NavHostController, vm: AuthViewModel) { //

    val textToSpeechParser by lazy { TextToSpeechParser(context = context) }
    val speechToTextParser by lazy { SpeechToTextParser(context) }

    val textToSpeechState by textToSpeechParser.textState.collectAsState()
    val speechParserState by speechToTextParser.voiceState.collectAsState()

    LaunchedEffect(key1 = "") {
        vm.generateReadContent(context)
    }

    var input by remember {
        mutableStateOf("")
    }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clickable {
                        navController.navigateUp()
                    } )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Reading")
            }

        }) },
        floatingActionButton = {

        }
    ) { padding ->

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    if(input ==vm.readText.value) {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = { navController.navigateUp() }) {
                            Text(text = "Done")
                        }

                    } else {
                        Icon(imageVector = Icons.Rounded.Error, contentDescription = null, tint = Color.Red, modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = { showBottomSheet = false }) {
                            Text(text = "Retry")
                        }

                    }

                }

            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 70.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.TopCenter) {

//                    AsyncImage(model = vm.readImage.value, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier
//                        .fillMaxWidth()
//                        .height(150.dp))

                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    if (!vm.readText.value.isNullOrEmpty()) {
                        Text(text = vm.readText.value,  fontSize=40.sp)
                        //input = vm.readText.value
                    }

                    IconButton(
                        onClick =
                        {
                            if(textToSpeechState.isSpeaking) textToSpeechParser.stopTranslator() else textToSpeechParser.translateToSpeech(vm.readText.value) //textToSpeechComplete(vm.readText.value, textToSpeechParser, speechToTextParser)
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

                Text(text = input)

                Button(onClick = {
                    if (speechParserState.isSpeaking) {
                        speechToTextParser.stopListening()
                        input = speechParserState.spokenText
                        showBottomSheet = !showBottomSheet
                    } else {
                        speechToTextParser.startListening()
                    }
                }) {
                    AnimatedContent(targetState = speechParserState.isSpeaking, label = "listen") { isSpeaking ->
                        if (isSpeaking) {
                            Text(text = "Stop Reading")
                        } else {
                            Text(text = "Start Reading")
                        }
                    }
                    //Text(text = "Read")
                }

            }

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildWriteScreen(context: Context, navController: NavHostController, vm: AuthViewModel) { //

    val lines = remember { mutableStateListOf<Line>() }
    val drawScope = CanvasDrawScope()
    val size = Size(400f, 400f)
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    val canvas = androidx.compose.ui.graphics.Canvas(bitmap)

    //val image = DrawToImage(orgBitmap = null)

    LaunchedEffect(key1 = "write") {
        vm.generateWriteContent()
    }

    var input by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


    Scaffold(
        topBar = { TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clickable {
                        navController.navigateUp()
                    } )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Writing")
            }

        }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                vm.checkImageResemblance(input, bitmap)
                showBottomSheet != showBottomSheet
            }) {
                Icon(imageVector = Icons.Rounded.Send, contentDescription = null)
            }
        }
    ) { padding ->

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    if(vm.writeResponse.value.contains("Yes")) {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = { navController.navigateUp() }) {
                            Text(text = "Done")
                        }

                    } else {
                        Icon(imageVector = Icons.Rounded.Error, contentDescription = null, tint = Color.Red, modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = { showBottomSheet = false }) {
                            Text(text = "Retry")
                        }

                    }

                }

            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier
                .fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                if(!vm.writeText.value.isNullOrEmpty()) {
                    Text(text = vm.writeText.value, fontSize=30.sp)
                    input  = vm.writeText.value
                }


                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color.White)
                    .pointerInput(true) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            val line = Line(
                                start = change.position - dragAmount,
                                end = change.position
                            )
                            lines.add(line)
                        }
                    }){
                    lines.forEach { line ->
                        drawLine(
                            color = line.color,
                            start = line.start,
                            end = line.end,
                            strokeWidth = line.strokeWidth.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    this.draw(density = Density(1f),
                        layoutDirection = LayoutDirection.Ltr,
                        canvas = canvas,
                        size = size) {
                        lines.forEach { line ->
                            drawLine(
                                color = line.color,
                                start = line.start,
                                end = line.end,
                                strokeWidth = line.strokeWidth.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

//                drawScope.draw(
//        density = Density(1f),
//        layoutDirection = LayoutDirection.Ltr,
//        canvas = canvas,
//        size = size
//    ) {
//        drawRect(color = Color.White, topLeft = Offset.Zero, size=size)
//        drawLine(
//            color = color,
//            start = Offset.Zero,
//            end = Offset(size.width, size.height),
//            strokeWidth = width,
//            cap = StrokeCap.Round
//        )
//    }


            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildStoryScreen(context: Context, navController: NavHostController, vm: AuthViewModel) { //

    val textToSpeechParser by lazy { TextToSpeechParser(context = context) }
    val textToSpeechState by textToSpeechParser.textState.collectAsState()

    LaunchedEffect(key1 = "story") {
        vm.generateStoryContent()
    }
    var input by remember { mutableStateOf("") }
    Scaffold(
        topBar = { TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clickable {
                        navController.navigateUp()
                    } )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Story Time")
            }

        }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { if(textToSpeechState.isSpeaking) textToSpeechParser.stopTranslator() else textToSpeechParser.translateToSpeech(input) }) {
                AnimatedContent(targetState = textToSpeechState.isSpeaking, label = "volume") { isSpeaking ->
                    if (isSpeaking) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.VolumeOff, contentDescription = null)
                    } else {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null)
                    }
                }
            }
        }
    ) { padding ->

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top=60.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(
                    rememberScrollState()
                )) {
                //items() {
                    if (!vm.storyText.value.isNullOrEmpty())  {
                        Text(text = vm.storyText.value)
                        input = vm.storyText.value
                    }
//                AsyncImage(model = vm.storyImages.value[0], contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier
//                    .width(150.dp)
//                    .height(100.dp))
                //}
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDrawScreen(context: Context, navController: NavHostController, vm: AuthViewModel) { //

    //val image = DrawToImage(orgBitmap = null)

    val lines = remember { mutableStateListOf<Line>() }
    val drawScope = CanvasDrawScope()
    val size = Size(400f, 400f)
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    val canvas = androidx.compose.ui.graphics.Canvas(bitmap)

    LaunchedEffect(key1 = "draw") {
        vm.generateDrawContent()
    }

    var input by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


    Scaffold(
        topBar = { TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clickable {
                        navController.navigateUp()
                    } )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Drawing")
            }

        }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                Log.d("Clicked", "${bitmap.height}")
                Log.d("Input", input)
                vm.checkImageResemblance(input, bitmap)
                showBottomSheet != showBottomSheet
            }) {
                Icon(imageVector = Icons.Rounded.Send, contentDescription = null)
            }
        }
    ) { padding ->

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    if(vm.writeResponse.value.contains("Yes")) {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = { navController.navigateUp() }) {
                            Text(text = "Done")
                        }

                    } else {
                        Icon(imageVector = Icons.Rounded.Error, contentDescription = null, tint = Color.Red, modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = { showBottomSheet = false }) {
                            Text(text = "Retry")
                        }

                    }

                }

            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                , horizontalAlignment = Alignment.CenterHorizontally) {

                if (!vm.drawText.value.isNullOrEmpty()) {
                    Text(text = "Draw this: ${vm.drawText.value}")
                    input = vm.drawText.value
                }

                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color.White)
                    .pointerInput(true) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            val line = Line(
                                start = change.position - dragAmount,
                                end = change.position
                            )
                            lines.add(line)
                        }
                    }){
                    lines.forEach { line ->
                        drawLine(
                            color = line.color,
                            start = line.start,
                            end = line.end,
                            strokeWidth = line.strokeWidth.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    this.draw(density = Density(1f),
                        layoutDirection = LayoutDirection.Ltr,
                        canvas = canvas,
                        size = size) {
                        lines.forEach { line ->
                            drawLine(
                                color = line.color,
                                start = line.start,
                                end = line.end,
                                strokeWidth = line.strokeWidth.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildShadeScreen(context: Context, navController: NavHostController, vm: AuthViewModel) { //

    LaunchedEffect(key1 = "shade") {
        vm.generateShadeContent(context)
    }

    val lines = remember { mutableStateListOf<Line>() }
    val drawScope = CanvasDrawScope()
    val size = Size(400f, 400f)
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    val canvas = androidx.compose.ui.graphics.Canvas(bitmap)

    val input = vm.shadeText.value
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedColor by remember { mutableStateOf(Color.Black) }

    //val image =  DrawToImage(color = selectedColor, width = 5f, vm.shadeImage.value?.asImageBitmap())

    Scaffold(
        topBar = { TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clickable {
                        navController.navigateUp()
                    } )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Shading")
            }

        }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                vm.checkImageResemblance(input, bitmap)
                showBottomSheet != showBottomSheet
            }) {
                Icon(imageVector = Icons.Rounded.Send, contentDescription = null)
            }
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                Icon(imageVector = Icons.Rounded.Circle, contentDescription = null, tint = Color.Black, modifier = Modifier.clickable {
                    selectedColor = Color.Black
                })
                Icon(imageVector = Icons.Rounded.Circle, contentDescription = null, tint = Color.Red, modifier = Modifier.clickable {
                    selectedColor = Color.Red
                })
                Icon(imageVector = Icons.Rounded.Circle, contentDescription = null, tint = Color.Yellow, modifier = Modifier.clickable {
                    selectedColor = Color.Yellow
                })
                Icon(imageVector = Icons.Rounded.Circle, contentDescription = null, tint = Color.Green, modifier = Modifier.clickable {
                    selectedColor = Color.Green
                })
                Icon(imageVector = Icons.Rounded.Circle, contentDescription = null, tint = Color.Blue, modifier = Modifier.clickable {
                    selectedColor = Color.Blue
                })
            }
        }
    ) { padding ->

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    if(vm.writeResponse.value.contains("Yes")) {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = { navController.navigateUp() }) {
                            Text(text = "Done")
                        }

                    } else {
                        Icon(imageVector = Icons.Rounded.Error, contentDescription = null, tint = Color.Red, modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(onClick = { showBottomSheet = false }) {
                            Text(text = "Retry")
                        }

                    }

                }

            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {

//                Canvas(modifier = Modifier.fillMaxSize()) {
//
//                    drawImage(image)
//                }

                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.White)
                    .pointerInput(true) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            val line = Line(
                                start = change.position - dragAmount,
                                end = change.position
                            )
                            lines.add(line)
                        }
                    }){
                    lines.forEach { line ->
                        drawLine(
                            color = selectedColor,
                            start = line.start,
                            end = line.end,
                            strokeWidth = 5f,
                            cap = StrokeCap.Round
                        )
                    }

                    vm.shadeImage.value?.asImageBitmap()?.let { this.drawImage(it, Offset.Zero) }

                    this.draw(density = Density(1f),
                        layoutDirection = LayoutDirection.Ltr,
                        canvas = canvas,
                        size = size) {

                        vm.shadeImage.value?.asImageBitmap()?.let { this.drawImage(it, Offset.Zero) }

                        lines.forEach { line ->
                            drawLine(
                                color = line.color,
                                start = line.start,
                                end = line.end,
                                strokeWidth = line.strokeWidth.toPx(),
                                cap = StrokeCap.Round
                            )
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

    object Scanner: NavScreen("Scanner")

    object Assess: NavScreen("Assess")

    object Shade: NavScreen("Shade")

    object ChildRead: NavScreen("ChildRead")

    object ChildWrite: NavScreen("childWrite")

    object ChildStory: NavScreen("childStory")

    object ChildDraw: NavScreen("ChildDraw")

    object ChildShade: NavScreen("ChildSchade")

}


data class Line (
    val start:Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 1.dp
)


