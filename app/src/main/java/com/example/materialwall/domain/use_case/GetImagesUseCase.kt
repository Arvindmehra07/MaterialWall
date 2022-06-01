package com.example.materialwall.domain.use_case

import androidx.paging.PagingData
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.domain.model.ImageRequest
import com.example.materialwall.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetImagesUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    private val currentTimestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
    operator fun invoke(
        imageRequest: ImageRequest,
    ): Flow<PagingData<ImageInfoEntity>> {
        return repository.getImages(imageRequest, currentTimestamp)
    }
}
