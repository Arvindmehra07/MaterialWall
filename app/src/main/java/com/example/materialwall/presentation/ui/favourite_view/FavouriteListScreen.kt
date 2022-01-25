package com.example.materialwall.presentation.ui.favourite_view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.materialwall.presentation.ui.common.ImageList

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun FavouriteListScreen(
   viewModel: FavouriteListViewModel = hiltViewModel(),
   navController: NavController
){
    val state = viewModel.state
    val images = state.value.images
    ImageList(imageListConstant = images, navController = navController, isPagingEnabled = false)
    if(state.value.isLoading){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
    if(state.value.error.isNotEmpty()){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.error)
        ) {
            Text(
                text = "Something went wrong!",
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}