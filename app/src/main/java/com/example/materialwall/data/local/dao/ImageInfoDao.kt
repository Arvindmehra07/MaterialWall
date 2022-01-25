package com.example.materialwall.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.materialwall.data.local.entity.ImageInfoEntity


@Dao
interface ImageInfoDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertImageInfo(imageInfoEntity: List<ImageInfoEntity>)

    @Query("DELETE FROM imageinfoentity WHERE `query` = :query")
    suspend fun deleteImageInfo(query: String)

    @Query("SELECT * FROM imageinfoentity WHERE `query` = :query and timestamp = :timestamp")
    fun getImageInfo(query: String, timestamp: String): PagingSource<Int, ImageInfoEntity>

    @Query("SELECT * FROM imageinfoentity WHERE imageId = :imageId")
    suspend fun getImageById(imageId : String) : ImageInfoEntity

    @Query("UPDATE imageinfoentity SET isFavourite = :isFavourite WHERE imageId = :imageId")
    suspend fun updateFavouriteImageById(isFavourite :Int, imageId : String)

    @Query("SELECT * FROM imageinfoentity WHERE isFavourite = 1")
    suspend fun getFavourites() : List<ImageInfoEntity>

}