package com.example.materialwall.presentation.ui.favourite_view

import com.example.materialwall.domain.model.Image

data class FavouriteListState (
    val isLoading : Boolean = false,
    val error : String = "",
    val images : List<Image> = emptyList()
)