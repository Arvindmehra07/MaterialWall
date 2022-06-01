package com.example.materialwall.presentation.ui.image_view

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.materialwall.common.Constants
import com.example.materialwall.common.LoadImageUtil
import com.example.materialwall.common.Resource
import com.example.materialwall.domain.use_case.GetImageByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagePreviewViewModel @Inject constructor(
    private val loadImageUtil: LoadImageUtil,
    private val getImageByIdUseCase: GetImageByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(ImagePreviewState())
    val state = _state

    init {
        savedStateHandle.get<String>(Constants.IMAGE_ID)?.let { imageId ->
            getImageById(imageId = imageId)
        }
    }


    private fun getImageById(imageId : String){
        getImageByIdUseCase.invoke(imageId).onEach {result ->
            when(result){
                is Resource.Success -> {
                    _state.value = ImagePreviewState(image = result.data)
                }
                is Resource.Loading -> {
                    _state.value = ImagePreviewState(isLoading = true)
                }
                is Resource.Error -> {
                    _state.value = ImagePreviewState(error = "An unexpected error occurred!")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateFavorite(imageId: String, isFavourite : Int){
        viewModelScope.launch {
            getImageByIdUseCase.updateFavourite(imageId = imageId, isFavourite)
        }
    }

    fun getBitmapUri(bitmap: Bitmap, id: String): Uri? {
        return loadImageUtil.saveImage(bitmap, id)
    }
}