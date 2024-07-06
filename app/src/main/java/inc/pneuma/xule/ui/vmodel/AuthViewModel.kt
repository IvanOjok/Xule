package inc.pneuma.xule.ui.vmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    val _selectedMethod =  mutableStateOf("")
    val selectedMethod = _selectedMethod.value



}