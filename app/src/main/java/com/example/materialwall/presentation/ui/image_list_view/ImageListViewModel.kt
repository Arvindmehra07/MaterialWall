package com.example.materialwall.presentation.ui.image_list_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.domain.model.ImageRequest
import com.example.materialwall.domain.use_case.GetImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ImageListViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase
) : ViewModel() {
    var pagingDataFlow : Flow<PagingData<ImageInfoEntity>>? = null

    private val imageRequest = ImageRequest(
        id = null,
        searchTerm = "Wallpaper",
        imageType = "all",
        category = null,
        order = null,
        perPage = null,
        page = 1,
        editorsChoice = true,
        minWidth = 1080,
        minHeight = 1920
    )

    init {
        getImages(imageRequest)
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun getImages(
        imageRequest: ImageRequest,
    ) {
        pagingDataFlow = getImagesUseCase.invoke(imageRequest = imageRequest).cachedIn(viewModelScope)
    }

    fun getImagesBySearch(searchTerm: String? = null, category: String? = null){
        val imageRequest = ImageRequest(
            id = null,
            searchTerm = searchTerm,
            imageType = "all",
            category = category,
            order = null,
            perPage = null,
            page = 1,
            editorsChoice = true,
            minWidth = 1080,
            minHeight = 1920
        )
        getImages(imageRequest)
    }
}