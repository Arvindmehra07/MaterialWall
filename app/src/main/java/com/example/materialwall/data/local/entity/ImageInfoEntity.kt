package com.example.materialwall.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.materialwall.domain.model.Image


@Entity
data class ImageInfoEntity(
    @PrimaryKey val image: Image,
    val query: String,
    val timestamp: String,
    val batchId : Int,
    val imageId : String,
    val isFavourite : Int
) /*{
  constructor() : this(Image(0,"","","","",0,0, ""), "","",0,"",0)
}*/


