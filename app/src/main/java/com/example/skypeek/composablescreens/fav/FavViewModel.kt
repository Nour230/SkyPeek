package com.example.skypeek.composablescreens.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.models.ResponseStateFav
import com.example.skypeek.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavViewModel(val repo: WeatherRepository) : ViewModel() {
    private val _favList = MutableStateFlow<ResponseStateFav>(ResponseStateFav.Loading)
    val favList = _favList.asStateFlow()

    private val _isDeleted = MutableSharedFlow<String>()
    val isDelete = _isDeleted.asSharedFlow()

    init {
        gerFavList()
    }

    fun gerFavList() {
        viewModelScope.launch {
            try {
                val response = repo.getAllLocations()
                response.catch { ex ->
                    _favList.value = ResponseStateFav.Error(ex)
                }.collect {
                    _favList.value = ResponseStateFav.Success(it)
                }
            } catch (e: Exception) {
                _favList.value = ResponseStateFav.Error(e)
            }
        }
    }

    fun deleteFromRoom(location: LocationPOJO) {
        viewModelScope.launch {
            try {
                repo.deleteLocation(location)
                _isDeleted.emit("item deleted from favorite")
            } catch (e: Exception) {
                _favList.value = ResponseStateFav.Error(e)
            }
        }
    }

}


class FavFactory(val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavViewModel(repo) as T
    }
}