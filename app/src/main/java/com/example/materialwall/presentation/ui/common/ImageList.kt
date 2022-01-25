package com.example.materialwall.presentation.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import coil.compose.rememberImagePainter
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.domain.model.Image
import com.example.materialwall.presentation.Screen
import com.example.materialwall.presentation.ui.theme.Shapes

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ImageList(imagePagingList : LazyPagingItems<ImageInfoEntity>? = null, imageListConstant : List <Image>? = null, isPagingEnabled: Boolean, navController: NavController?) {
    var itemCount = 0
    if(isPagingEnabled){
        if (imagePagingList != null) {
            itemCount = imagePagingList.itemCount
        }
    }else {
        imageListConstant?.let { itemCount = it.size }
    }

    if (itemCount != 0) {
        LazyVerticalGrid(
            contentPadding = PaddingValues(16.dp),
            cells = GridCells.Fixed(2)
        ) {
            items(itemCount) { index ->
                val imageInfo = if(isPagingEnabled) imagePagingList?.get(index)?.image else imageListConstant?.get(index)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = Shapes.medium,
                    elevation = 0.dp,
                    onClick = {
                        navController?.navigate(route = Screen.ImagePreviewScreen.route + "/${imageInfo?.id}")
                    },
                    backgroundColor = MaterialTheme.colorScheme.surface
                ) {
                    ImageWithPlaceholder(
                        painter = rememberImagePainter(imageInfo?.webFormUrl,
                        builder = {crossfade(true)} ),
                        contentDescription = "image",
                        modifier = Modifier
                            .size(300.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = { Placeholder()}
                    )
                }
            }
        }
    }
}