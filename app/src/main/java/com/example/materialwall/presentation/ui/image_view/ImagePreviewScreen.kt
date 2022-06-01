package com.example.materialwall.presentation.ui.image_view

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.materialwall.R
import com.example.materialwall.domain.model.Image
import com.example.materialwall.presentation.ui.common.ImageWithPlaceholder
import com.example.materialwall.presentation.ui.common.Placeholder
import com.example.materialwall.presentation.ui.theme.MaterialWallTheme
import com.example.materialwall.presentation.ui.theme.Shapes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.roundToInt
import androidx.compose.material3.MaterialTheme as Material3Theme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageViewScreen(
    viewModel: ImagePreviewViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val image = state.image
    val context = LocalContext.current

    if (state.isLoading) {
        Box(
            contentAlignment = Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(color = Material3Theme.colorScheme.primary)
        }
    } else if (state.error.isNotBlank()) {
        Box(
            contentAlignment = Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = stringResource(id = R.string.error))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .padding(4.dp),
                shape = Shapes.medium,
                elevation = 0.dp,
                backgroundColor = Material3Theme.colorScheme.surface
            ) {
                ImageWithPlaceholder(
                    painter = rememberImagePainter(image?.fullHdUrl,
                        builder = {
                            crossfade(true)
                        }),
                    contentDescription = "image",
                    modifier = Modifier.size(960.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = { Placeholder() }
                )
            }

            BottomContentComposable(context, viewModel, image)
        }
    }

}

@Preview
@Composable
fun PreviewImage() {
    MaterialWallTheme {
        //BottomContentComposable()
    }
}

private fun downloadImage(
    id: String,
    viewModel: ImagePreviewViewModel,
    url: String,
    context: Context,
    isSetWallpaper: Boolean
): Flow<Boolean> = flow {
    emit(true)
    val imageLoader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .build()
    try {
        val bitmap = (imageLoader.execute(request).drawable as BitmapDrawable).bitmap
        viewModel.getBitmapUri(bitmap, id)!!
        if (isSetWallpaper) {
            setWallpaper(context, bitmap)
            emit(false)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Wallpaper set!", Toast.LENGTH_SHORT).show()
            }
        } else {
            emit(false)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download saved in your gallery.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    } catch (e: Exception) {
        emit(false)
        Log.d("Error", e.message ?: "Error occured!")
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun setWallpaper(context: Context, bitmap: Bitmap) {
    val wallpaperIntent = WallpaperManager.getInstance(context)
    try {
        wallpaperIntent.setBitmap(bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun BottomContentComposable(context: Context, viewModel: ImagePreviewViewModel, image: Image?) {
    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { openDialog.value = false },
            containerColor = Material3Theme.colorScheme.primaryContainer,
            text = {
                Column(modifier = Modifier
                    .height(680.dp)
                    .width(680.dp)) {
                    Text(
                        text = "Drag and adjust",
                        textAlign = TextAlign.Center,
                        style = Material3Theme.typography.headlineSmall,
                        color = Material3Theme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ZoomableImage(imageUrl = image?.imageUrl!!)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Set as wallpaper",
                        textAlign = TextAlign.Center,
                        style = Material3Theme.typography.bodyMedium,
                        color = Material3Theme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {}
        )
    }
    Box(contentAlignment = Center) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            var isBusyForDownload by remember {
                mutableStateOf(false)
            }
            var isBusyForSetWallpaper by remember {
                mutableStateOf(false)
            }
            var isFavouriteSet by remember {
                mutableStateOf(false)
            }
            val scope = rememberCoroutineScope()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (!isBusyForDownload) {
                    IconPlusTextComposable(
                        image = R.drawable.ic_round_download_24,
                        contentDes = "Download"
                    ) {
                        openDialog.value = true
//                        scope.launch {
//                            downloadImage(
//                                "MaterialWall_${image?.id}",
//                                viewModel,
//                                image?.imageUrl!!,
//                                context,
//                                false
//                            ).collect { value -> isBusyForDownload = value }
//                        }
                    }
                } else {
                    CircularProgressIndicator(
                        color = Material3Theme.colorScheme.primary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                if (!isBusyForSetWallpaper) {
                    IconPlusTextComposable(
                        image = R.drawable.ic_round_wallpaper_24,
                        contentDes = "Set as wallpaper"
                    ) {
                        scope.launch {
                            downloadImage(
                                "MaterialWall_${image?.id}",
                                viewModel,
                                image?.imageUrl!!,
                                context,
                                true
                            ).collect { value -> isBusyForSetWallpaper = value }
                        }

                    }
                } else {
                    CircularProgressIndicator(
                        color = Material3Theme.colorScheme.primary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                IconPlusTextComposable(
                    image = R.drawable.baseline_favorite_24,
                    contentDes = "Favourite",
                    iconTint = if (isFavouriteSet) Material3Theme.colorScheme.tertiary else Material3Theme.colorScheme.primary
                ) {
                    if (image?.isFavourite == 0) {
                        viewModel.updateFavorite(imageId = image.id.toString(), isFavourite = 1)
                        image.isFavourite = 1
                        isFavouriteSet = true
                    } else {
                        viewModel.updateFavorite(imageId = image?.id.toString(), isFavourite = 0)
                        image?.isFavourite = 0
                        isFavouriteSet = false
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            ImageDetailComposable(context, image?.user!!, image.imageWidth, image.imageHeight)
        }
    }
}

@Composable
fun IconPlusTextComposable(
    image: Int,
    contentDes: String,
    iconTint: androidx.compose.ui.graphics.Color? = null,
    onClickAction: () -> Unit
) {
    val tint = Material3Theme.colorScheme.primary
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(10.dp)
            .clickable { onClickAction.invoke() }
    ) {
        Icon(
            painter = painterResource(id = image),
            contentDescription = contentDes,
            modifier = Modifier.size(32.dp),
            tint = (iconTint ?: tint)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = contentDes,
            style = Material3Theme.typography.bodySmall,
            color = Material3Theme.colorScheme.primary
        )
    }
}

@Composable
fun ImageDetailComposable(context: Context, userName: String, imgWidth: Int, imgHeight: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextPlusTextComposable(stringResource(id = R.string.str_by), userName)
        Divider(
            Modifier
                .height(30.dp)
                .width(1.dp),
            color = Material3Theme.colorScheme.onSurfaceVariant
        )
        TextPlusTextComposable(
            itemName = stringResource(id = R.string.str_size),
            itemDes = "$imgWidth * $imgHeight"
        )
        Divider(
            Modifier
                .height(30.dp)
                .width(1.dp),
            color = Material3Theme.colorScheme.onSurfaceVariant
        )

        Column(horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(width = 100.dp, height = 50.dp)
                .clickable {
                    startActivity(
                        context,
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://pixabay.com")),
                        null
                    )
                }) {
            Text(
                text = stringResource(id = R.string.str_provided_by),
                style = Material3Theme.typography.bodySmall,
                color = Material3Theme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_pixabay_logo),
                contentDescription = "Pixabay",
                tint = Material3Theme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun TextPlusTextComposable(itemName: String, itemDes: String) {
    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.size(width = 100.dp, height = 50.dp)
    ) {
        Text(
            text = itemName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            style = Material3Theme.typography.bodySmall,
            color = Material3Theme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = itemDes,
            style = Material3Theme.typography.bodyMedium,
            color = Material3Theme.colorScheme.secondary
        )
    }
}

@Composable
fun ZoomableImage(imageUrl: String) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    var offsetX by remember { mutableStateOf(0f) }
    Box(Modifier.wrapContentSize()) {
        ImageWithPlaceholder(
            painter = rememberImagePainter(imageUrl),
            contentDescription = "",
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                // add transformable to listen to multitouch transformation events
                // after offset
                .transformable(state = state)
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX += delta
                    }
                ),
            placeholder = { Placeholder()}
        )
    }
}

@Composable
fun ImageDialog(imageUrl: String) {
    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { openDialog.value = false },
            containerColor = Material3Theme.colorScheme.primaryContainer,
            title = {
                Text(text = "Drag and adjust", style = Material3Theme.typography.headlineSmall, color = Material3Theme.colorScheme.onSurface)
            },
            text = {
                    ZoomableImage(imageUrl = imageUrl)
            },
            confirmButton = {
                Text(text = "Set as wallpaper", style = Material3Theme.typography.bodyMedium, color = Material3Theme.colorScheme.onSurface)
            }
        )
    }
}

