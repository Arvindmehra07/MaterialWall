package com.example.materialwall.domain.use_case

import com.example.materialwall.common.Resource
import com.example.materialwall.domain.model.Image
import com.example.materialwall.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetImageByIdUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    operator fun invoke(imageId : String) : Flow<Resource<Image>>{
        return repository.getImageById(imageId)
    }

    suspend fun updateFavourite(imageId: String, isFavourite : Int) {
        return repository.updateFavourite(imageId = imageId, isFavourite)
    }
}