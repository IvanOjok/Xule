package inc.pneuma.xule.data.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import inc.pneuma.xule.data.QUESTION
import inc.pneuma.xule.data.SUBJECT
import inc.pneuma.xule.data.TEXT_OR_SPEECH
import inc.pneuma.xule.data.TOPIC
import inc.pneuma.xule.data.USER_GRADE
import inc.pneuma.xule.data.USER_NAME
import inc.pneuma.xule.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val ds: DataStore<Preferences>
): PreferenceRepository {
    override suspend fun saveSpeechOrText(value: String) {
        ds.edit {
            it[TEXT_OR_SPEECH] = value
        }
    }

    override suspend fun getSpeechOrText(): Flow<String> = ds.data.map {
        it[TEXT_OR_SPEECH] ?: "00.00"
    }

    override suspend fun saveUserName(value: String) {
        ds.edit {
            it[USER_NAME] = value
        }
    }

    override suspend fun getUserName(): Flow<String> = ds.data.map {
        it[USER_NAME] ?: ""
    }

    override suspend fun saveUserGrade(value: String) {
        ds.edit {
            it[USER_GRADE] = value
        }
    }

    override suspend fun getUserGrade(): Flow<String> = ds.data.map {
        it[USER_GRADE] ?: ""
    }

    override suspend fun saveSubjectQuestion(value: String) {
        ds.edit {
            it[SUBJECT] = value
        }
    }

    override suspend fun getSubjectQuestion(): Flow<String> = ds.data.map {
        it[SUBJECT] ?: ""
    }

    override suspend fun saveSubjectTopic(value: String) {
        ds.edit {
            it[TOPIC] = value
        }
    }

    override suspend fun getSubjectTopic(): Flow<String> = ds.data.map {
        it[TOPIC] ?: ""
    }


    override suspend fun saveExactQuestion(value: String) {
        ds.edit {
            it[QUESTION] = value
        }
    }

    override suspend fun getExactQuestion(): Flow<String> = ds.data.map {
        it[QUESTION] ?: ""
    }



}