package com.example.materialwall.domain.repository

import androidx.paging.PagingData
import com.example.materialwall.common.Resource
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.domain.model.Image
import com.example.materialwall.domain.model.ImageRequest
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImages(imageRequest: ImageRequest, timestamp: String): Flow<PagingData<ImageInfoEntity>>

    fun getImageById(imageId : String) : Flow<Resource<Image>>

    suspend fun updateFavourite(imageId: String, isFavourite : Int)

    fun getFavourites() : Flow<Resource<List<ImageInfoEntity>>>
}