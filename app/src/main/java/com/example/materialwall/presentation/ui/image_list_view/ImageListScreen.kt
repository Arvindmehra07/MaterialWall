package com.example.materialwall.presentation.ui.image_list_view


import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.materialwall.R
import com.example.materialwall.data.local.entity.ImageInfoEntity
import com.example.materialwall.presentation.Screen
import com.example.materialwall.presentation.ui.category_view.CategoryScreen
import com.example.materialwall.presentation.ui.common.ImageList
import com.example.materialwall.presentation.ui.favourite_view.FavouriteListScreen
import com.example.materialwall.presentation.ui.theme.MaterialWallTheme
import java.util.*
import androidx.compose.material3.MaterialTheme as Material3Theme

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ImageListScreen(
    viewModel: ImageListViewModel = hiltViewModel(),
    navController: NavController
) {
    val imageListItems = viewModel.pagingDataFlow?.collectAsLazyPagingItems()
    val itemCount = imageListItems?.itemCount!!
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }
    val navHostController = rememberNavController()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                viewModel = viewModel,
                itemList = imageListItems,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { BottomBar(navHostController) },
        content = {
            Navigation(
                viewModel = viewModel,
                imageList = imageListItems,
                navControllerMain = navController,
                navHostController = navHostController
            )
        })

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(imageList: LazyPagingItems<ImageInfoEntity>, navController: NavController) {

    imageList.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = Material3Theme.colorScheme.primary)
                }
            }
            loadState.append is LoadState.Loading -> {}
            loadState.append is LoadState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.error),
                        color = Material3Theme.colorScheme.onError
                    )
                }
            }
        }
    }
    ImageList(imagePagingList = imageList, navController = navController, isPagingEnabled = true)
}

@Composable
fun BottomBar(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(1) }
    val items = listOf(stringResource(R.string.category), stringResource(R.string.home), stringResource(R.string.favourites))
    val icons = listOf(Icons.Filled.List, Icons.Filled.Home, Icons.Filled.Favorite)
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    navController.navigate(item)
                    selectedItem = index
                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppBar(
    viewModel: ImageListViewModel,
    itemList: LazyPagingItems<ImageInfoEntity>,
    scrollBehavior: TopAppBarScrollBehavior
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchTerm by remember { mutableStateOf("Wallpaper") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val openDialog = remember { mutableStateOf(false) }

    MediumTopAppBar(
        title = {
            Text(
                text = if (isSearchActive) "" else searchTerm
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                modifier = Modifier.padding(16.dp)
            )
        },
        modifier = Modifier.wrapContentSize(align = Center),
        scrollBehavior = scrollBehavior,
        actions = {
            if (isSearchActive) {
                TextField(
                    value = searchTerm,
                    placeholder = { Text(text = stringResource(R.string.search)) },
                    onValueChange = { searchTerm = it },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Material3Theme.colorScheme.onBackground,
                        disabledTextColor = Color.Transparent,
                        backgroundColor = Material3Theme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.round_search_24),
                            contentDescription = "Search icon",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {
                                    searchClickAction(
                                        searchTerm = searchTerm,
                                        viewModel = viewModel,
                                        itemList = itemList
                                    )
                                    keyboardController?.hide()
                                    isSearchActive = false
                                }
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        searchClickAction(
                            searchTerm = searchTerm,
                            viewModel = viewModel,
                            itemList = itemList
                        )
                        isSearchActive = false
                        keyboardController?.hide()
                    }),
                    singleLine = true,
                    modifier = Modifier
                        .widthIn(min = 224.dp, max= 324.dp)
                        .wrapContentHeight()
                        .padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 4.dp)
                        .focusRequester(focusRequester)
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_clear_24),
                    contentDescription = "Clear icon",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable {
                            if (searchTerm.isBlank()) {
                                isSearchActive = false
                                searchTerm = "Wallpaper"
                            } else
                                searchTerm = ""
                        }
                )

                DisposableEffect(key1 = Unit) {
                    focusRequester.requestFocus()
                    onDispose { }
                }

            } else {
                Icon(
                    painter = painterResource(id = R.drawable.round_search_24),
                    contentDescription = "Search image",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable {
                            isSearchActive = true
                            searchTerm = ""
                        }
                )
            }
        },
        navigationIcon = {
            if (isSearchActive) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back icon",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable {
                            isSearchActive = false
                            searchTerm = "Wallpaper"
                        }
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.round_insights_24),
                    contentDescription = "About icon",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable { openDialog.value = true },
                )
            }
        }
    )


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            containerColor = Material3Theme.colorScheme.primaryContainer,
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.made_with),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Material3Theme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_favorite_24),
                            contentDescription = "Love",
                            modifier = Modifier.align(CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.str_by) + " "+ stringResource(R.string.maker_name),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Material3Theme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}


private fun searchClickAction(
    searchTerm: String = "",
    viewModel: ImageListViewModel,
    itemList: LazyPagingItems<ImageInfoEntity>
) {
    if (searchTerm.contains(":")) {
        val parts = searchTerm.split(":")
        viewModel.getImagesBySearch(category = parts[0], searchTerm = parts[1])
    } else {
        viewModel.getImagesBySearch(searchTerm = searchTerm)
    }
    itemList.refresh()
}

@Preview
@Composable
fun ShowDialogPreview() {
    MaterialWallTheme {
        // CategoryScreen()
    }
}


@Composable
fun Navigation(
    viewModel: ImageListViewModel,
    imageList: LazyPagingItems<ImageInfoEntity>,
    navControllerMain: NavController,
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.CategoryScreen.route) {
            CategoryScreen(imageList, viewModel = viewModel, navHostController = navHostController)
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(imageList, navControllerMain)
        }
        composable(Screen.FavouriteScreen.route) {
            FavouriteListScreen(navController = navControllerMain)
        }
    }
}

