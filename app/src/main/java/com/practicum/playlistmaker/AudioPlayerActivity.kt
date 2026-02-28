package com.practicum.playlistmaker

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
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
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var track: Track? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimerRunnable = Runnable {
        updateTimer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        initViews()
        bindTrackFromIntent()
        finishActivity()
        initPlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimerRunnable)
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

        track = intent.parcelable<Track>(KEY_TRACK)
    }

    private fun finishActivity() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getCoverArtwork(track: Track) = track.artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")

    private fun bindTrackFromIntent() {
        val currentTrack = track
        if (currentTrack != null) {
        Glide.with(trackImage)
            .load(getCoverArtwork(currentTrack))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(R.dimen.big_track_image_corner_radius.dxToPx(this)))
            .into(trackImage)

        trackName.text = currentTrack.trackName ?: getString(R.string.unknown_track)
        artistName.text = currentTrack.artistName ?: getString(R.string.unknown_artist)
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime) ?: getString(R.string.unknown_time)
        trackAlbum.text = currentTrack.collectionName ?: getString(R.string.unknown_album)
        trackYear.text = getYearFromReleaseDate(currentTrack.releaseDate)
        trackGenre.text = currentTrack.primaryGenreName ?: getString(R.string.unknown_genre)
        trackCountry.text = currentTrack.country ?: getString(R.string.unknown_country)
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

    private fun preparePlayer() {
        val currentTrack = track
        if (currentTrack != null) {
            mediaPlayer.setDataSource(currentTrack.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playButton.setImageResource(R.drawable.btn_play)
                playerState = STATE_PREPARED
                handler.removeCallbacks (updateTimerRunnable)
                currentTime.text = getString(R.string.zero_time)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.btn_pause)
        playerState = STATE_PLAYING
        updateTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.btn_play)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimerRunnable)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun initPlayer() {
        preparePlayer()
        playButton.setOnClickListener { playbackControl() }
    }

    private fun updateTimer() {
        currentTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        handler.postDelayed(updateTimerRunnable, TIMER_UPDATE_DELAY)
    }

    companion object {
        const val KEY_TRACK = "track"

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val TIMER_UPDATE_DELAY = 300L
    }
}