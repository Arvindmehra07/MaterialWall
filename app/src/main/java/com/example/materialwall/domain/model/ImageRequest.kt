package com.example.materialwall.domain.model

data class ImageRequest(
    val id: String?,
    val searchTerm: String?,
    val imageType: String,
    val category: String?,
    val order: String?,
    val perPage: Int?,
    val page: Int,
    val editorsChoice: Boolean,
    val minWidth: Int,
    val minHeight: Int
)
