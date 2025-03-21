//package com.example.skypeek.composablescreens.fav
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.combine
//
//class FavViewModel : ViewModel() {
//    //first state whether the search is happening or not
//    private val _isSearching = MutableStateFlow(false)
//    val isSearching = _isSearching.asStateFlow()
//
//    //second state the text typed by the user
//    private val _searchText = MutableStateFlow("")
//    val searchText = _searchText.asStateFlow()
//
//    //third state the list to be filtered
//    private val _countriesList = MutableStateFlow(countries)
//    val countriesList = searchText
//        .combine(_countriesList) { text, countries ->//combine searchText with _contriesList
//            if (text.isBlank()) { //return the entery list of countries if not is typed
//                countries
//            }
//            countries.filter { country ->// filter and return a list of countries based on the text the user typed
//                country.uppercase().contains(text.trim().uppercase())
//            }
//        }.stateIn(//basically convert the Flow returned from combine operator to StateFlow
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),//it will allow the StateFlow survive 5 seconds before it been canceled
//            initialValue = _countriesList.value
//        )
//
//    fun onSearchTextChange(text: String) {
//        _searchText.value = text
//    }
//
//    fun onToogleSearch() {
//        _isSearching.value = !_isSearching.value
//        if (!_isSearching.value) {
//            onSearchTextChange("")
//        }
//    }
//
//}