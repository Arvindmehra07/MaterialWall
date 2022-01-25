package com.example.materialwall.presentation.ui.category_view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.presentation.Screen
import com.example.materialwall.presentation.ui.Category
import com.example.materialwall.presentation.ui.image_list_view.ImageListViewModel
import com.example.materialwall.presentation.ui.theme.Shapes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryCard(
    name: String,
    url: String,
    navHostController: NavHostController,
    imageList: LazyPagingItems<ImageInfoEntity>,
    onClickAction: () -> Unit
) {
    Box(
        Modifier.padding(16.dp, 8.dp)
    ) {
        Card(
            shape = Shapes.medium,
            backgroundColor = MaterialTheme.colorScheme.secondary,
            onClick = {
                onClickAction.invoke()
                navHostController.navigate(Screen.HomeScreen.route)
                imageList.refresh()
            }
        ) {
            Box {
                Image(
                    painter = rememberImagePainter(url,
                        builder = {
                            transformations(RoundedCornersTransformation())
                        }),
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                        .drawWithCache {
                            val gradient = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = size.height / 3,
                                endY = size.height
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(gradient, blendMode = BlendMode.Multiply)
                            }
                        }
                )
                Text(
                    text = name,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryScreen(
    imageList: LazyPagingItems<ImageInfoEntity>,
    navHostController: NavHostController,
    viewModel: ImageListViewModel
) {
    val itemList = Category.CategoryList.list
    val layoutMargin = 16.dp
    LazyVerticalGrid(
        contentPadding = PaddingValues(top = layoutMargin, start = layoutMargin, end = layoutMargin, bottom = 76.dp),
        cells = GridCells.Fixed(2)
    ) {
        items(itemList.size) { index ->
            val item = itemList[index]
            CategoryCard(
                name = item.name,
                url = item.url,
                navHostController = navHostController,
                imageList = imageList
            ) { viewModel.getImagesBySearch(searchTerm = item.name, category = item.name) }
        }
    }
}
