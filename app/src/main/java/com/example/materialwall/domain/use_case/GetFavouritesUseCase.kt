package com.example.materialwall.domain.use_case

import com.example.materialwall.common.Resource
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouritesUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    operator fun invoke() : Flow<Resource<List<ImageInfoEntity>>> {
        return repository.getFavourites()
    }
}