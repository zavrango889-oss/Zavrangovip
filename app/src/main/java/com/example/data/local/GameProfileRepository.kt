package com.example.data.local

import kotlinx.coroutines.flow.Flow

class GameProfileRepository(private val dao: GameProfileDao) {

    val allProfiles: Flow<List<GameProfileEntity>> = dao.getAllProfiles()

    suspend fun getProfileById(id: Long): GameProfileEntity? {
        return dao.getProfileById(id)
    }

    suspend fun insertProfile(profile: GameProfileEntity): Long {
        return dao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: GameProfileEntity) {
        dao.updateProfile(profile)
    }

    suspend fun deleteProfile(profile: GameProfileEntity) {
        dao.deleteProfile(profile)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun resetToDefaults() {
        dao.clearAll()
        AppDatabase.populateDefaultProfiles(dao)
    }

    suspend fun ensureDefaultsExist() {
        if (dao.getProfileCount() == 0) {
            AppDatabase.populateDefaultProfiles(dao)
        }
    }
}
