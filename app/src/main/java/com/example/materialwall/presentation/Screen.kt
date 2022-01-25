package com.example.materialwall.presentation

sealed class Screen(val route : String){
    object ImageListScreen : Screen("image_list_screen")
    object ImagePreviewScreen : Screen("image_preview_screen")
    object CategoryScreen : Screen("Category")
    object FavouriteScreen : Screen("Favourites")
    object HomeScreen :Screen("Home")
}
