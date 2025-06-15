package com.practicum.playlistmaker.search

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.GsonBuilder
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.AudioPlayerActivity
import com.practicum.playlistmaker.search.iTunes.ITunesApi
import com.practicum.playlistmaker.search.iTunes.ITunesResponse
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.hideKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var currentQuery: String = DEFAULT_QUERY
    private val trackList = ArrayList<Track>()
    private var historyTrackList = ArrayList<Track>()

    private lateinit var searchInput: EditText
    private lateinit var errorTextView: TextView
    private lateinit var errorImageView: ImageView
    private lateinit var retrySearchButton: Button
    private lateinit var clearSearchButton: ImageView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyTrackAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackHistoryRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create()

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    private val itunesApiService = retrofit.create(ITunesApi::class.java)

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
        searchHistory.saveHistoryTracks(historyTrackList)
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
    }

    private fun finishActivity() {
        val toolbarSearch = findViewById<MaterialToolbar>(R.id.toolbar_search)
        toolbarSearch.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(trackList) { onTrackSelected(it) }
        trackRecyclerView.adapter = trackAdapter
        trackRecyclerView.layoutManager = LinearLayoutManager(this)

        historyTrackAdapter = TrackAdapter(historyTrackList) { onTrackSelected(it) }
        trackHistoryRecyclerView.adapter = historyTrackAdapter
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
        }
    }

    private fun setupSearchFieldBehavior() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.isVisible = !s.isNullOrEmpty()
                currentQuery = s.toString()

                searchHistoryContainer.isVisible = searchInput.hasFocus() && searchInput.text.isEmpty() && historyTrackList.isNotEmpty()
                trackRecyclerView.isVisible = !searchHistoryContainer.isVisible
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
            searchHistory.clearSearchHistory()
            historyTrackList.clear()
            historyTrackAdapter.notifyDataSetChanged()
            searchHistoryContainer.isVisible = false
        }
    }

    private fun loadSearchHistory() {
        val app = applicationContext as App
        searchHistory = SearchHistory(app.getSearchPrefs())
        historyTrackList = searchHistory.getHistoryTracks()
    }

    private fun searchTracks() {
        val query = searchInput.text.toString()
        itunesApiService.search(query)
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.clear()
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                showMessage("")
                            } else {
                                showMessage(
                                    getString(R.string.nothing_found),
                                    getDrawable(R.drawable.nothing_found))
                            }
                        }
                        else -> {
                            showMessage(
                                getString(R.string.connection_problems),
                                getDrawable(R.drawable.network_error),
                                showRetryButton = true)

                        }
                    }
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    showMessage(
                        getString(R.string.connection_problems),
                        getDrawable(R.drawable.network_error),
                        showRetryButton = true)
                }
            })
    }

    private fun showMessage(
        message: String,
        image: Drawable? = null,
        showRetryButton: Boolean = false
    ) {
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
        trackAdapter.notifyDataSetChanged()
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
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun onTrackSelected(track: Track) {
        addTrackToHistory(track)
        openAudioPlayer(track)
    }

    private fun notifyAdapterItemRemoved(position: Int) {
        historyTrackAdapter.notifyItemRemoved(position)
        historyTrackAdapter.notifyItemRangeChanged(position, historyTrackList.size)
    }

    private fun addTrackAndNotifyAdapter(track: Track) {
        historyTrackList.add(0, track)
        historyTrackAdapter.notifyItemInserted(0)
    }

    companion object {
        const val KEY_QUERY = "SEARCH_QUERY"
        const val DEFAULT_QUERY = ""
    }
}
