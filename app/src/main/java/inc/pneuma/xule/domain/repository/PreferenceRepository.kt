package inc.pneuma.xule.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    suspend fun saveSpeechOrText(value:String)

    suspend fun getSpeechOrText(): Flow<String>

    suspend fun saveUserName(value: String)

    suspend fun getUserName(): Flow<String>

    suspend fun saveUserGrade(value: String)

    suspend fun getUserGrade(): Flow<String>
}