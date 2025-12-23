package com.practicum.playlistmaker

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.search.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var addToPlaylistButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var likeButton: ImageView
    private lateinit var currentTime: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        initViews()
        bindTrackFromIntent()
        finishActivity()
    }

    private fun initViews() {
        backButton = findViewById(R.id.btn_back)

        trackImage = findViewById(R.id.track_image)
        trackName = findViewById(R.id.name_of_track)
        artistName = findViewById(R.id.name_of_artist)
        addToPlaylistButton = findViewById(R.id.btn_add_to_playlist)
        playButton = findViewById(R.id.btn_play_and_pause)
        likeButton = findViewById(R.id.btn_like)
        currentTime = findViewById(R.id.current_time)
        trackTime = findViewById(R.id.time)
        trackAlbum = findViewById(R.id.name_of_album)
        trackYear = findViewById(R.id.name_of_year)
        trackGenre = findViewById(R.id.name_of_genre)
        trackCountry = findViewById(R.id.name_of_country)
    }

    private fun finishActivity() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getCoverArtwork(track: Track) = track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")


    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private fun bindTrackFromIntent() {
        val track = intent.parcelable<Track>("track")
        if (track != null) {
        Glide.with(trackImage)
            .load(getCoverArtwork(track))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(R.dimen.big_track_image_corner_radius.dxToPx(this)))
            .into(trackImage)

        trackName.text = track.trackName ?: getString(R.string.unknown_track)
        artistName.text = track.artistName ?: getString(R.string.unknown_artist)
        currentTime.text = getString(R.string.unknown_time)
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime) ?: getString(R.string.unknown_time)
        trackAlbum.text = track.collectionName ?: getString(R.string.unknown_album)
        trackYear.text = getYearFromReleaseDate(track.releaseDate)
        trackGenre.text = track.primaryGenreName ?: getString(R.string.unknown_genre)
        trackCountry.text = track.country ?: getString(R.string.unknown_country)
        }
    }

    private fun getYearFromReleaseDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return getString(R.string.unknown_year)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            if (date != null)
                outputFormat.format(date)
            else
                getString(R.string.unknown_year)
        } catch (e: Exception) {
            getString(R.string.unknown_year)
        }
    }
}