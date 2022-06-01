package com.example.materialwall.presentation.ui.image_view

import android.graphics.Bitmap
import com.example.materialwall.domain.model.Image

data class ImagePreviewState(
    val image : Image? = null,
    val isLoading : Boolean = false,
    val error : String = "",
    val bitmap : Bitmap? = null
)
