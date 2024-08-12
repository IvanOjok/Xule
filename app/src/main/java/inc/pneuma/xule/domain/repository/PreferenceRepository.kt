package inc.pneuma.xule.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    suspend fun saveSpeechOrText(value:String)

    suspend fun getSpeechOrText(): Flow<String>

    suspend fun saveUserName(value: String)

    suspend fun getUserName(): Flow<String>

    suspend fun saveUserGrade(value: String)

    suspend fun getUserGrade(): Flow<String>

    suspend fun saveSubjectQuestion(value:String)

    suspend fun getSubjectQuestion() : Flow<String>

    suspend fun saveSubjectTopic(value:String)

    suspend fun getSubjectTopic() : Flow<String>

    suspend fun saveExactQuestion(value:String)

    suspend fun getExactQuestion(): Flow<String>
}