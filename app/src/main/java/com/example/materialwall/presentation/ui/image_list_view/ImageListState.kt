package com.example.materialwall.presentation.ui.image_list_view

import com.example.materialwall.domain.model.Image

data class ImageListState (
    val isLoading : Boolean = false,
    val error : String = "",
    val images : List<Image> = emptyList()
)
