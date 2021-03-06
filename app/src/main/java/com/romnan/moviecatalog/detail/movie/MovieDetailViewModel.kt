package com.romnan.moviecatalog.detail.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.moviecatalog.core.data.Resource
import com.romnan.moviecatalog.core.domain.usecase.MovieCatalogUseCase
import com.romnan.moviecatalog.core.presentation.movie.MovieDetail
import com.romnan.moviecatalog.core.util.MovieMapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MovieDetailViewModel(private val useCase: MovieCatalogUseCase) : ViewModel() {
    private val _movie = MutableLiveData<MovieDetail>()
    val movie: LiveData<MovieDetail> = _movie

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getMovieDetail(movieId: Int) {
        viewModelScope.launch {
            useCase.getMovieDetail(movieId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isLoading.postValue(true)
                    is Resource.Success -> {
                        val data = resource.data?.let { MovieMapper.domainToDetail(it) }
                        _movie.postValue(data)
                        _isLoading.postValue(false)
                    }
                    is Resource.Error -> {
                        _errorMessage.postValue(resource.message)
                        _isLoading.postValue(false)
                    }
                }
            }
        }
    }

    fun setFavoriteMovie(moviePresented: MovieDetail, newState: Boolean) {
        val movie = MovieMapper.detailToDomain(moviePresented)
        useCase.setFavoriteMovie(movie, newState)
    }
}