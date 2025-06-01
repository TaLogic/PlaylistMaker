package com.practicum.playlistmaker.search

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
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

    private lateinit var searchEditText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var errorImageView: ImageView
    private lateinit var retryButton: Button
    private lateinit var trackAdapter: TrackAdapter

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.et_search)
        errorTextView = findViewById(R.id.error_text)
        errorImageView = findViewById(R.id.iv_error)
        retryButton = findViewById(R.id.btn_update)

        //Завершить активити по кнопке назад
        val toolbarSearch = findViewById<MaterialToolbar>(R.id.toolbar_search)
        toolbarSearch.setNavigationOnClickListener {
            finish()
        }

        //Очистка текста в строке поиска
        val clearButton = findViewById<ImageView>(R.id.btn_clear)
        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            clearTrackResults()
        }

        //Логика EditText
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                currentQuery = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(textWatcher)

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        //Список треков
        val trackRecyclerView = findViewById<RecyclerView>(R.id.rv_tracks)
        trackAdapter = TrackAdapter(trackList)
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = trackAdapter

        retryButton.setOnClickListener { searchTracks() }
    }

    private fun searchTracks() {
        val query = searchEditText.text.toString()
        iTunesService.search(query)
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

    // Сообщение об ошибке
    private fun showMessage(
        message: String,
        image: Drawable? = null,
        showRetryButton: Boolean = false
    ) {
        if (message.isNotEmpty()) {
            errorTextView.visibility = View.VISIBLE
            errorTextView.text = message
            clearTrackResults()
        } else {
            errorTextView.visibility = View.GONE
        }

        if (image != null) {
            errorImageView.visibility = View.VISIBLE
            errorImageView.setImageDrawable(image)
        } else {
            errorImageView.visibility = View.GONE
        }

        if (showRetryButton) {
            retryButton.visibility = View.VISIBLE
        } else {
            retryButton.visibility = View.GONE
        }
    }

    private fun clearTrackResults() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, currentQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentQuery = savedInstanceState.getString(KEY_QUERY, DEFAULT_QUERY)
        searchEditText.setText(currentQuery)
    }

    companion object {
        const val KEY_QUERY = "SEARCH_QUERY"
        const val DEFAULT_QUERY = ""
    }
}
