package com.example.materialwall.data.remote

import com.example.materialwall.common.Constants
import com.example.materialwall.data.remote.dto.ImageSearchDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {
    @GET("api/")
    suspend fun getImages(
        @Query("key") key: String = Constants.API_KEY,
        @Query("id") id: String? = null,
        @Query(value ="q", encoded = true) searchTerm: String?,
        @Query("image_type") imageType: String,
        @Query("category") category: String?,
        @Query("order") order: String?,
        @Query("per_page") perPage: Int?,
        @Query("page") page: Int?,
        @Query("editors_choice") editorsChoice: Boolean,
        @Query("safesearch") safeSearch: Boolean = true,
        @Query("min_width") minWidth: Int,
        @Query("min_height") minHeight: Int
    ): ImageSearchDto
}
