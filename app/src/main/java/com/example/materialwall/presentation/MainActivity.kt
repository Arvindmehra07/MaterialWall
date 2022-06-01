package com.example.materialwall.presentation

import android.graphics.Point
import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.materialwall.presentation.ui.image_list_view.ImageListScreen
import com.example.materialwall.presentation.ui.image_view.ImageViewScreen
import com.example.materialwall.presentation.ui.theme.MaterialWallTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        setContent {
            MaterialWallTheme {
                window?.statusBarColor = MaterialTheme.colorScheme.inverseOnSurface.toArgb()
                window?.navigationBarColor = MaterialTheme.colorScheme.inverseOnSurface.toArgb()
                rememberSystemUiController().statusBarDarkContentEnabled = !isSystemInDarkTheme()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.ImageListScreen.route
                    ) {
                        composable(route = Screen.ImageListScreen.route) {
                            ImageListScreen(navController = navController)
                        }
                        composable(route = Screen.ImagePreviewScreen.route + "/{imageId}") {
                            ImageViewScreen()
                        }
                    }
                }
            }
        }
    }
}