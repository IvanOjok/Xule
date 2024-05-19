package inc.pneuma.xule.ui.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import inc.pneuma.xule.widgets.XuleExposedDropdownMenuBox

@Composable
fun XuleMainScreen(name: String, isLoggedIn:Boolean = false, modifier: Modifier = Modifier) {
    if (isLoggedIn) {
        Text(
            text = "Hello $name, \n Welcome back. How are You today?",
            modifier = modifier
        )
    } else {
        Column() {
            Text(
                text = "Welcome, I'm Xule; An AI Powered Learning Assistant. \n What mode of communication are You comfortable with? If You prefer text conversations only, say or select Text. If You prefer speech only, say Audio but if You would like both, say or select Both",
                modifier = modifier
            )
            XuleExposedDropdownMenuBox(list = arrayOf("Text", "Audio", "Both"), onElementSelected = { /**save selected option to preferences via view model*/ })
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
