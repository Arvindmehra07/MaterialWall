package com.example.materialwall.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageInfoRemoteKeyEntity(
    @PrimaryKey val id : Int,
    val query: String?,
    val prevKey: Int?,
    val nextKey: Int?
)