package com.example.materialwall.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.materialwall.data.local.entity.ImageInfoRemoteKeyEntity

@Dao
interface ImageInfoRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys : List<ImageInfoRemoteKeyEntity>)

    @Query("DELETE FROM imageinforemotekeyentity")
    suspend fun clearRemoteKeys()

    @Query("SELECT * FROM imageinforemotekeyentity WHERE  id = :id")
    suspend fun remoteKeysById(id: Int): ImageInfoRemoteKeyEntity?

}