package com.romnan.moviecatalog.ui.discover.tvseries

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.romnan.moviecatalog.R
import com.romnan.moviecatalog.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_popular_shows.*

class DiscoverTvSeriesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_popular_shows, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            val factory = ViewModelFactory.getInstance()
            val viewModel = ViewModelProvider(this, factory)[DiscoverTvSeriesViewModel::class.java]

            val tvSeriesAdapter = DiscoverTvSeriesAdapter()

            viewModel.getPopularTvSeries()

            viewModel.getPopularTvSeries().observe(this, { tvSeries ->
                tvSeriesAdapter.setShows(tvSeries)
                progress_bar.visibility = View.GONE
                Log.d(TAG, "onActivityCreated: ${tvSeries.size}")
            })

            with(rv_pop_show) {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = tvSeriesAdapter
            }
        }
    }

    companion object {
        private const val TAG = "DiscoverTvSeriesFrag"
    }
}