package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StyleDao {
    // User Profile Queries
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Wardrobe Queries
    @Query("SELECT * FROM wardrobe_items ORDER BY timestamp DESC")
    fun getAllWardrobeItems(): Flow<List<WardrobeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWardrobeItem(item: WardrobeItem)

    @Delete
    suspend fun deleteWardrobeItem(item: WardrobeItem)

    // Saved Looks Queries
    @Query("SELECT * FROM saved_looks ORDER BY timestamp DESC")
    fun getAllSavedLooks(): Flow<List<SavedLook>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedLook(look: SavedLook)

    @Delete
    suspend fun deleteSavedLook(look: SavedLook)

    @Update
    suspend fun updateSavedLook(look: SavedLook)
}
