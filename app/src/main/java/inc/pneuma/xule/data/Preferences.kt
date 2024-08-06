package inc.pneuma.xule.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


private const val USER_PREFERENCES_NAME = "user_preferences"

val Context.appPreferenceDatastore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

val TEXT_OR_SPEECH = stringPreferencesKey("text_or_speech")

val USER_NAME = stringPreferencesKey("user_name")

val USER_GRADE = stringPreferencesKey("user_grade")