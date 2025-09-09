package com.example.addresssearchapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.addresssearchapp.data.model.LocationItem
import com.example.addresssearchapp.data.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddressSearchViewModel(private val repository: LocationRepository) : ViewModel() {

    private val _searchResults = MutableLiveData<List<LocationItem>>()
    val searchResults: LiveData<List<LocationItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _currentQuery = MutableLiveData<String>()
    val currentQuery: LiveData<String> = _currentQuery

    private var searchJob: Job? = null

    fun searchAddresses(query: String) {
        // Cancel previous search
        searchJob?.cancel()

        _currentQuery.value = query

        if (query.trim().isEmpty()) {
            _searchResults.value = emptyList()
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            // Debounce for 1 second
            delay(1000)

            _isLoading.value = true
            _error.value = null

            repository.searchLocations(query.trim()).fold(
                onSuccess = { locations ->
                    _searchResults.value = locations
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                    _searchResults.value = emptyList()
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
}