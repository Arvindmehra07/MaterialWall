package com.example.materialwall.data.remote.dto

import com.example.materialwall.data.local.entity.ImageInfoEntity

data class ImageSearchDto(
    val hits: List<ImageDto>,
    val total: Int,
    val totalHits: Int
)

fun ImageSearchDto.toImageInfoEntity(batchId: Int, query: String, timestamp: String) : List<ImageInfoEntity> {
    val list = ArrayList<ImageInfoEntity>()
    for (hit in hits){
        list.add(ImageInfoEntity(image = hit.toImage(), query = query, batchId = batchId, imageId = hit.id.toString(), timestamp = timestamp, isFavourite = 0))
    }
    return list
}