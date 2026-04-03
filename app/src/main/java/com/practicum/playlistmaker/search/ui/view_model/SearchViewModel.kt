package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksResult
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.search_history.SearchHistoryInteractor
import com.practicum.playlistmaker.search.ui.models.ErrorType
import com.practicum.playlistmaker.search.ui.models.SearchState

class SearchViewModel(private val tracksInteractor: TracksInteractor, private val searchHistoryInteractor: SearchHistoryInteractor) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(tracksInteractor: TracksInteractor, searchHistoryInteractor: SearchHistoryInteractor): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchViewModel(tracksInteractor, searchHistoryInteractor)
                }
            }
    }

    private var latestSearchText: String? = null

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

//    private val showToastEvent = SingleLiveEvent<String>()
//    fun observeShowToast(): LiveData<String> = showToastEvent

    private val historyTracksLiveData = MutableLiveData<List<Track>>(searchHistoryInteractor.getHistoryTracks())
    fun observeHistoryTracks(): LiveData<List<Track>> = historyTracksLiveData

    private val handler = Handler(Looper.getMainLooper())


    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTracks(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY

        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun search(text: String) {
        searchTracks(text)
    }

    private fun searchTracks(newSearchText: String) {
        if (newSearchText.isEmpty()) {
            renderState(SearchState.NoQuery)
        } else {
            renderState(SearchState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: TracksResult) {
                    handler.post {

                        when (foundTracks) {
                            is TracksResult.Success -> renderState(SearchState.Content(foundTracks.tracks))

                            is TracksResult.Empty -> renderState(SearchState.Empty)

                            is TracksResult.NetworkError -> renderState(SearchState.Error(ErrorType.Network))

                            is TracksResult.ServerError -> renderState(SearchState.Error(ErrorType.Server))

                            is TracksResult.EmptyQuery -> renderState((SearchState.NoQuery))

                        }
                    }
                }
            })
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun saveToHistory(track: Track) {
        searchHistoryInteractor.saveToHistory(track)
        historyTracksLiveData.postValue(searchHistoryInteractor.getHistoryTracks())
    }

    fun clearTrackHistory() {
        searchHistoryInteractor.clearSearchHistory()
        historyTracksLiveData.postValue(emptyList())
    }

    fun cancelPendingSearch() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        latestSearchText = ""
    }
}