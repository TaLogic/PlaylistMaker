package com.practicum.playlistmaker.ui.audioplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.domain.player.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.player.PlaybackListener
import com.practicum.playlistmaker.dxToPx
import com.practicum.playlistmaker.formatDuration
import com.practicum.playlistmaker.parcelable
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
    private var track: Track? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimerRunnable = Runnable {
        updateTimer()
    }

    lateinit var audioPlayerInteractor: AudioPlayerInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        initViews()
        bindTrackFromIntent()
        finishActivity()
        initPlayer()
        initPlaybackListener()
    }

    override fun onPause() {
        super.onPause()
        audioPlayerInteractor.pauseTrack()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerInteractor.releasePlayer()
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

        audioPlayerInteractor = Creator.provideAudioPlayerInteractor()
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
        trackTime.text = currentTrack.trackTime?.let { SimpleDateFormat("mm:ss", Locale.getDefault()).format(it) } ?: getString(R.string.unknown_time)
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

    private fun initPlaybackListener() {
        audioPlayerInteractor.setPlaybackListener(object : PlaybackListener {
            override fun onCompleted() {
                playButton.setImageResource(R.drawable.btn_play)
                handler.removeCallbacks (updateTimerRunnable)
                currentTime.text = getString(R.string.zero_time)
            }

            override fun onPlay() {
                playButton.setImageResource(R.drawable.btn_pause)
                updateTimer()
            }

            override fun onPause() {
                playButton.setImageResource(R.drawable.btn_play)
                handler.removeCallbacks(updateTimerRunnable)
            }
        })
    }

    private fun initPlayer() {
        track?.let { audioPlayerInteractor.preparePlayer(it.previewUrl ) }
        playButton.setOnClickListener { audioPlayerInteractor.playbackControl()  }
    }

    private fun updateTimer() {
        currentTime.text = formatDuration(audioPlayerInteractor.getCurrentPosition())
        handler.postDelayed(updateTimerRunnable, TIMER_UPDATE_DELAY)
    }

    companion object {
        const val KEY_TRACK = "track"

        private const val TIMER_UPDATE_DELAY = 300L
    }
}