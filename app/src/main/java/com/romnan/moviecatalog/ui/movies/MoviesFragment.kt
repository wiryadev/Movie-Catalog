package com.romnan.moviecatalog.ui.movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.romnan.moviecatalog.R
import kotlinx.android.synthetic.main.fragment_movies.*


class MoviesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_movies, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.NewInstanceFactory()
        )[MoviesViewModel::class.java]

        if (activity != null) {
            val movies = viewModel.retrieveMovieData()
            val movieAdapter = MovieAdapter()
            movieAdapter.setMovies(movies)
            Log.d(Companion.TAG, "onActivityCreated: ${movies.size}")

            with(rv_movie) {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = movieAdapter
            }
        }
    }

    companion object {
        private const val TAG = "MoviesFragment"
    }
}