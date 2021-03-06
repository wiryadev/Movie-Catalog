package com.romnan.moviecatalog.core.data

import com.romnan.moviecatalog.core.data.source.local.LocalDataSource
import com.romnan.moviecatalog.core.data.source.remote.RemoteDataSource
import com.romnan.moviecatalog.core.data.source.remote.network.ApiResponse
import com.romnan.moviecatalog.core.data.source.remote.response.MovieResponse
import com.romnan.moviecatalog.core.data.source.remote.response.TvSeriesResponse
import com.romnan.moviecatalog.core.domain.model.movie.Movie
import com.romnan.moviecatalog.core.domain.model.tvseries.TvSeries
import com.romnan.moviecatalog.core.domain.repository.IMovieCatalogRepository
import com.romnan.moviecatalog.core.util.AppExecutors
import com.romnan.moviecatalog.core.util.MovieMapper
import com.romnan.moviecatalog.core.util.TvSeriesMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class MovieCatalogRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : IMovieCatalogRepository {

    override fun getDiscoverMovies(): Flow<Resource<List<Movie>>> =
        object : NetworkBoundResource<List<Movie>, List<MovieResponse>>() {
            override fun loadFromDB(): Flow<List<Movie>> =
                localDataSource.getDiscoverMovies().map { movieEntities ->
                    movieEntities.map { MovieMapper.entityToDomain(it) }
                }

            override fun shouldFetch(data: List<Movie>?): Boolean = data == null || data.isEmpty()

            override suspend fun createCall(): Flow<ApiResponse<List<MovieResponse>>> =
                remoteDataSource.getDiscoverMovies()

            override suspend fun saveCallResult(data: List<MovieResponse>) {
                val moviesList = data.map { MovieMapper.responseToEntity(it) }
                localDataSource.insertMovies(moviesList)
            }
        }.asFlow()

    override fun getDiscoverTvSeries(): Flow<Resource<List<TvSeries>>> =
        object :
            NetworkBoundResource<List<TvSeries>, List<TvSeriesResponse>>() {
            override fun loadFromDB(): Flow<List<TvSeries>> =
                localDataSource.getDiscoverTvSeries().map { tvSeriesEntities ->
                    tvSeriesEntities.map { TvSeriesMapper.entityToDomain(it) }
                }

            override fun shouldFetch(data: List<TvSeries>?): Boolean =
                data == null || data.isEmpty()

            override suspend fun createCall(): Flow<ApiResponse<List<TvSeriesResponse>>> =
                remoteDataSource.getDiscoverTvSeries()

            override suspend fun saveCallResult(data: List<TvSeriesResponse>) {
                val tvSeriesList = data.map { TvSeriesMapper.responseToEntity(it) }
                localDataSource.insertTvSeries(tvSeriesList)
            }
        }.asFlow()

    override fun getMovieDetail(movieId: Int): Flow<Resource<Movie>> =
        object :
            NetworkBoundResource<Movie, MovieResponse>() {
            override fun loadFromDB(): Flow<Movie> = localDataSource.getMovieDetail(movieId)
                .map { MovieMapper.entityToDomain(it) }

            override fun shouldFetch(data: Movie?): Boolean = data?.tagline == "null"

            override suspend fun createCall(): Flow<ApiResponse<MovieResponse>> =
                remoteDataSource.getMovieDetail(movieId)

            override suspend fun saveCallResult(data: MovieResponse) {
                val movie = MovieMapper.responseToEntity(data)
                localDataSource.insertMovies(listOf(movie))
            }
        }.asFlow()

    override fun getTvSeriesDetail(tvSeriesId: Int): Flow<Resource<TvSeries>> =
        object :
            NetworkBoundResource<TvSeries, TvSeriesResponse>() {
            override fun loadFromDB(): Flow<TvSeries> =
                localDataSource.getTvSeriesDetail(tvSeriesId)
                    .map { TvSeriesMapper.entityToDomain(it) }

            override fun shouldFetch(data: TvSeries?): Boolean = data?.tagline == "null"

            override suspend fun createCall(): Flow<ApiResponse<TvSeriesResponse>> =
                remoteDataSource.getTvSeriesDetail(tvSeriesId)

            override suspend fun saveCallResult(data: TvSeriesResponse) {
                val tvSeries = TvSeriesMapper.responseToEntity(data)
                localDataSource.insertTvSeries(listOf(tvSeries))
            }
        }.asFlow()

    override fun getFavoriteMovies(): Flow<List<Movie>> =
        localDataSource.getFavoriteMovies().map { movieEntities ->
            movieEntities.map { MovieMapper.entityToDomain(it) }
        }

    override fun getFavoriteTvSeries(): Flow<List<TvSeries>> =
        localDataSource.getFavoriteTvSeries().map { tvSeriesEntities ->
            tvSeriesEntities.map { TvSeriesMapper.entityToDomain(it) }
        }

    override fun setFavoriteMovie(movie: Movie, state: Boolean) {
        val movieEntity = MovieMapper.domainToEntity(movie)
        appExecutors.diskIO().execute { localDataSource.setFavoriteMovie(movieEntity, state) }
    }

    override fun setFavoriteTvSeries(tvSeries: TvSeries, state: Boolean) {
        val tvSeriesEntity = TvSeriesMapper.domainToEntity(tvSeries)
        appExecutors.diskIO().execute { localDataSource.setFavoriteTvSeries(tvSeriesEntity, state) }
    }
}