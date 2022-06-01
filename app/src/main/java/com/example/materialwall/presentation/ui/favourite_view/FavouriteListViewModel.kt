package com.example.materialwall.presentation.ui.favourite_view

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.materialwall.common.Resource
import com.example.materialwall.domain.use_case.GetFavouritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FavouriteListViewModel @Inject constructor(
    private val getFavouritesUseCase: GetFavouritesUseCase
) : ViewModel() {
    private val _state = mutableStateOf(FavouriteListState())
    val state = _state

    init {
        getFavourites()
    }

    private fun getFavourites() {
        getFavouritesUseCase.invoke().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = result.data?.map { imageInfoEntity -> imageInfoEntity.image  }
                        ?.let { FavouriteListState(images = it) }!!
                }
                is Resource.Loading -> {
                    _state.value = FavouriteListState(isLoading = true)
                }
                is Resource.Error -> {
                    _state.value = FavouriteListState(error = "An unexpected error occurred!")
                }
            }
        }.launchIn(viewModelScope)
    }
}