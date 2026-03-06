package com.practicum.playlistmaker.ui.search

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.ui.audioplayer.AudioPlayerActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.hideKeyboard
import com.practicum.playlistmaker.domain.TracksResult
import com.practicum.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.ui.tracks.TracksAdapter

class SearchActivity : AppCompatActivity() {

    private var currentQuery: String = DEFAULT_QUERY
    private val trackList = ArrayList<Track>()
    private var historyTrackList = ArrayList<Track>()

    private lateinit var searchInput: EditText
    private lateinit var errorTextView: TextView
    private lateinit var errorImageView: ImageView
    private lateinit var retrySearchButton: Button
    private lateinit var clearSearchButton: ImageView
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var historyTracksAdapter: TracksAdapter
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackHistoryRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorContainer: LinearLayout

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTracks() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        finishActivity()
        loadSearchHistory()
        setupAdapters()
        setupListeners()
    }

    override fun onStop() {
        super.onStop()
        searchHistoryInteractor.saveHistoryTracks(historyTrackList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, currentQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentQuery = savedInstanceState.getString(KEY_QUERY, DEFAULT_QUERY)
        searchInput.setText(currentQuery)
    }

    private fun initViews() {
        searchInput = findViewById(R.id.et_search)
        errorTextView = findViewById(R.id.error_text)
        errorImageView = findViewById(R.id.iv_error)
        retrySearchButton = findViewById(R.id.btn_update)
        searchHistoryContainer = findViewById(R.id.search_history_container)
        clearHistoryButton = findViewById(R.id.btn_clear_history)
        clearSearchButton = findViewById(R.id.btn_clear)
        trackRecyclerView = findViewById(R.id.rv_tracks)
        trackHistoryRecyclerView = findViewById(R.id.rv_history_tracks)
        progressBar = findViewById(R.id.progressBar)
        errorContainer = findViewById(R.id.error_container)

        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    }

    private fun finishActivity() {
        val toolbarSearch = findViewById<MaterialToolbar>(R.id.toolbar_search)
        toolbarSearch.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupAdapters() {
        tracksAdapter = TracksAdapter(trackList) { onTrackSelected(it) }
        trackRecyclerView.adapter = tracksAdapter
        trackRecyclerView.layoutManager = LinearLayoutManager(this)

        historyTracksAdapter = TracksAdapter(historyTrackList) { onTrackSelected(it) }
        trackHistoryRecyclerView.adapter = historyTracksAdapter
        trackHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        clearSearchInput()
        setupSearchFieldBehavior()
        retrySearchButton.setOnClickListener { searchTracks() }
        setupClearHistoryButton()
    }

    private fun clearSearchInput() {
        clearSearchButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard()
            clearTrackResults()
            errorContainer.isVisible = false
        }
    }

    private fun setupSearchFieldBehavior() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.isVisible = !s.isNullOrEmpty()
                currentQuery = s.toString()

                searchHistoryContainer.isVisible = searchInput.hasFocus() && searchInput.text.isBlank() && historyTrackList.isNotEmpty()
                trackRecyclerView.isVisible = !searchHistoryContainer.isVisible

                if (currentQuery.isNotEmpty())
                    searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        searchInput.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryContainer.isVisible = hasFocus && searchInput.text.isEmpty() && historyTrackList.isNotEmpty()
            trackRecyclerView.isVisible = !searchHistoryContainer.isVisible

        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun setupClearHistoryButton() {
        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearSearchHistory()
            historyTrackList.clear()
            historyTracksAdapter.notifyDataSetChanged()
            searchHistoryContainer.isVisible = false
        }
    }

    private fun loadSearchHistory() {
        historyTrackList = ArrayList(searchHistoryInteractor.getHistoryTracks())
    }

    private fun searchTracks() {
        // Меняем видимость элементов перед выполнением запроса
        errorContainer.isVisible = false
        progressBar.isVisible = true

        val query = searchInput.text.toString()
        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: TracksResult) {
                runOnUiThread {
                    errorContainer.isVisible = true
                    progressBar.isVisible = false

                    when(result) {
                        is TracksResult.Success -> {
                            trackList.clear()
                            trackList.addAll(result.tracks)
                            tracksAdapter.notifyDataSetChanged()
                            showMessage("")
                        }
                        is TracksResult.Empty -> showMessage(
                            getString(R.string.nothing_found),
                            getDrawable(R.drawable.nothing_found))
                        is TracksResult.NetworkError ->  showMessage(
                            getString(R.string.connection_problems),
                            getDrawable(R.drawable.network_error),
                            showRetryButton = true)
                        is TracksResult.ServerError ->  showMessage(
                            getString(R.string.connection_problems),
                            getDrawable(R.drawable.network_error),
                            showRetryButton = true)
                        is TracksResult.EmptyQuery -> showMessage("")

                    }
                }
            }
        })
    }

    private fun showMessage(
        message: String,
        image: Drawable? = null,
        showRetryButton: Boolean = false
    ) {
        errorTextView.isVisible = message.isNotEmpty()
        errorImageView.isVisible = message.isNotEmpty()
        errorTextView.text = message
        if (message.isNotEmpty()) {
            clearTrackResults()
        }

        errorImageView.isVisible = image != null
        errorImageView.setImageDrawable(image)

        retrySearchButton.isVisible = showRetryButton
    }

    private fun clearTrackResults() {
        trackList.clear()
        tracksAdapter.notifyDataSetChanged()
    }

    private fun addTrackToHistory(track: Track) {
        if (historyTrackList.contains(track)) {
            val position = historyTrackList.indexOf(track)
            historyTrackList.remove(track)
            notifyAdapterItemRemoved(position)

            addTrackAndNotifyAdapter(track)
        } else {
            if (historyTrackList.size < 10) {
                addTrackAndNotifyAdapter(track)
            } else {
                val lastIndex = historyTrackList.lastIndex
                historyTrackList.removeAt(lastIndex)
                notifyAdapterItemRemoved(lastIndex)

                addTrackAndNotifyAdapter(track)
            }
        }
    }

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(AudioPlayerActivity.KEY_TRACK,track)
        startActivity(intent)
    }

    private fun onTrackSelected(track: Track) {
        addTrackToHistory(track)
        openAudioPlayer(track)
    }

    private fun notifyAdapterItemRemoved(position: Int) {
        historyTracksAdapter.notifyItemRemoved(position)
        historyTracksAdapter.notifyItemRangeChanged(position, historyTrackList.size)
    }

    private fun addTrackAndNotifyAdapter(track: Track) {
        historyTrackList.add(0, track)
        historyTracksAdapter.notifyItemInserted(0)
    }

    private fun searchDebounce() {
        handler.removeCallbacks (searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val KEY_QUERY = "SEARCH_QUERY"
        const val DEFAULT_QUERY = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}