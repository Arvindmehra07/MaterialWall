package com.example.materialwall.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.materialwall.data.local.dao.ImageInfoDao
import com.example.materialwall.data.local.dao.ImageInfoRemoteKeyDao
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.data.local.entity.ImageInfoRemoteKeyEntity


@Database(
    entities = [ImageInfoEntity::class, ImageInfoRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ImageInfoDatabase : RoomDatabase() {
    abstract val imageInfoDao: ImageInfoDao
    abstract val imageInfoRemoteKeyDao : ImageInfoRemoteKeyDao
}