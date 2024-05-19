package inc.pneuma.xule.ui.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import inc.pneuma.xule.widgets.XuleExposedDropdownMenuBox
import androidx.navigation.compose.rememberNavController

@Composable
fun XuleMainScreen(name: String, isLoggedIn:Boolean = false, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavScreen.Home.route) {
        composable(NavScreen.Home.route) {
            SignUpScreen()
        }

        composable(NavScreen.Welcome.route) {
            LoggedInFirstScreen(name)
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
@Composable
fun SignUpScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = "Welcome, I'm Xule; An AI Powered Learning Assistant. \n What mode of communication are You comfortable with? If You prefer text conversations only, say or select Text. If You prefer speech only, say Audio but if You would like both, say or select Both",
                modifier = Modifier,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(25.dp))
            XuleExposedDropdownMenuBox(
                list = arrayOf("Text", "Audio", "Both"),
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

sealed class NavScreen(val route: String) {

    object Splash : NavScreen("Splash")

    object Welcome: NavScreen("Welcome")

    //object

    object Home : NavScreen("Home")

    object PosterDetails : NavScreen("PosterDetails") {

        const val routeWithArgument: String = "PosterDetails/{posterId}"

        const val argument0: String = "posterId"
    }
}
