package com.example.materialwall.domain.model

data class Image(
    val id : Int,
    val webFormUrl : String,
    val imageUrl : String,
    val fullHdUrl : String,
    val largeUrl : String,
    val imageWidth : Int,
    val imageHeight : Int,
    val user : String,
    var isFavourite : Int = 0
)