package com.practicum.playlistmaker.search.ui.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.hideKeyboard
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.search_history.SearchHistoryInteractor
import com.practicum.playlistmaker.search.ui.TracksAdapter
import com.practicum.playlistmaker.search.ui.models.ErrorType
import com.practicum.playlistmaker.search.ui.models.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var historyTracksAdapter: TracksAdapter

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var searchViewModel: SearchViewModel

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var currentQuery: String = DEFAULT_QUERY
    private var textWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initInteractors()
        initViewModel()
        initAdapters()
        initToolbar()
        initListeners()
        observeViewModel()
        observeHistoryTracks()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, currentQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentQuery = savedInstanceState.getString(KEY_QUERY, DEFAULT_QUERY)
        binding.searchInput.setText(currentQuery)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.searchInput.removeTextChangedListener(it) }

    }

    /* ------------------------------ Initialization ------------------------------ */

    private fun initInteractors() {
        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    }

    private fun initViewModel() {
        searchViewModel = ViewModelProvider(
            this,
            SearchViewModel.getFactory(tracksInteractor, searchHistoryInteractor)
        ).get(
            SearchViewModel::class.java
        )
    }

    private fun initAdapters() {
        tracksAdapter = TracksAdapter { onTrackClicked(it) }
        binding.trackRecyclerView.adapter = tracksAdapter
        binding.trackRecyclerView.layoutManager = LinearLayoutManager(this)

        historyTracksAdapter = TracksAdapter { onTrackClicked(it) }
        binding.trackHistoryRecyclerView.adapter = historyTracksAdapter
        binding.trackHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initToolbar() {
        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }
    }

    /* ------------------------------ Listeners ------------------------------ */

    private fun initListeners() {
        setupSearchInput()
        setupEditorAction()
        setupClearSearchButton()
        setupClearHistoryButton()
        binding.retrySearchButton.setOnClickListener { searchViewModel.search(currentQuery) }
    }

    private fun setupSearchInput() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString().trim()
                binding.clearSearchButton.isVisible = s?.isNotEmpty() == true

                if (currentQuery.isNotEmpty()) {
                    showLoading()
                    searchViewModel.searchDebounce(currentQuery)
                } else {
                    showEmptyQuery()
                    searchViewModel.cancelPendingSearch()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.searchInput.addTextChangedListener(textWatcher)

        binding.searchInput.setOnFocusChangeListener { view, hasFocus -> updateSearchVisibility() }
    }

    private fun setupEditorAction() {
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchViewModel.search(currentQuery)
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun setupClearSearchButton() {
        binding.clearSearchButton.setOnClickListener {
            binding.searchInput.setText("")
            hideKeyboard()
            tracksAdapter.setTracks(emptyList())
            binding.errorContainer.isVisible = false
        }
    }

    private fun setupClearHistoryButton() {
        binding.clearHistoryButton.setOnClickListener {
            searchViewModel.clearTrackHistory()
            binding.searchHistoryContainer.isVisible = false
        }
    }

    private fun updateSearchVisibility() {
        val showHistory = binding.searchInput.hasFocus() &&
                binding.searchInput.text.isBlank() &&
                historyTracksAdapter.itemCount > 0

        binding.searchHistoryContainer.isVisible = showHistory
        binding.trackRecyclerView.isVisible = !showHistory // может убрать
    }

    /* ------------------------------ Track Click ------------------------------ */

    private fun onTrackClicked(track: Track) {
        openAudioPlayer(track)
        searchViewModel.saveToHistory(track)
    }

    private fun openAudioPlayer(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra(AudioPlayerActivity.Companion.KEY_TRACK, track)
            startActivity(intent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    /* ------------------------------ Rendering ------------------------------ */

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.NoQuery -> showEmptyQuery()
            is SearchState.Error -> {
                when (state.type) {
                    ErrorType.Network -> showError(
                        getString(R.string.connection_problems),
                        getDrawable(R.drawable.network_error),
                        true
                    )

                    ErrorType.Server -> showError(
                        getString(R.string.connection_problems),
                        getDrawable(R.drawable.network_error),
                        true
                    )
                }
            }

            is SearchState.Empty -> showEmpty(
                getString(R.string.nothing_found),
                getDrawable(R.drawable.nothing_found)
            )

            is SearchState.Content -> showContent(state.tracks)
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            errorContainer.isVisible = false
            trackRecyclerView.isVisible = false
            searchHistoryContainer.isVisible = false
        }
    }

    private fun showError(
        errorMessage: String,
        errorImage: Drawable?,
        showRetryButton: Boolean = false
    ) {
        binding.apply {
            progressBar.isVisible = false
            errorContainer.isVisible = true
            trackRecyclerView.isVisible = false
            searchHistoryContainer.isVisible = false

            errorTextView.text = errorMessage
            errorImage?.let { errorImageView.setImageDrawable(errorImage) }

            retrySearchButton.isVisible = showRetryButton
        }
    }

    private fun showEmpty(emptyMessage: String, errorImage: Drawable?) {
        showError(emptyMessage, errorImage)
    }

    private fun showContent(trackList: List<Track>) {
        binding.apply {
            progressBar.isVisible = false
            errorContainer.isVisible = false
            trackRecyclerView.isVisible = true
            searchHistoryContainer.isVisible = false
        }

        tracksAdapter.setTracks(trackList)
    }

    private fun showEmptyQuery() {
        binding.apply {
            progressBar.isVisible = false
            errorContainer.isVisible = false
            updateSearchVisibility()  // searchHistoryContainer.isVisible = true
            trackRecyclerView.isVisible = false
        }
    }

    /* ------------------------------ Observers ------------------------------ */

    private fun observeViewModel() {
        searchViewModel.observeState().observe(this) {
            render(it)
        }
    }

    private fun observeHistoryTracks() {
        searchViewModel.observeHistoryTracks().observe(this) {
            historyTracksAdapter.setTracks(it)
        }
    }

    companion object {
        const val KEY_QUERY = "SEARCH_QUERY"
        const val DEFAULT_QUERY = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}