package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.dxToPx
import com.practicum.playlistmaker.parcelable
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private var track: Track? = null

    lateinit var audioPlayerInteractor: AudioPlayerInteractor

    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var viewModel: AudioPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDependencies()
        initViewModel()
        observeViewModel()
        setupClickListener()
        finishActivity()
        bindTrack()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }


    private fun initDependencies() {
        track = intent.parcelable<Track>(KEY_TRACK)
        audioPlayerInteractor = Creator.provideAudioPlayerInteractor()
    }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                AudioPlayerViewModel.getFactory(audioPlayerInteractor, track?.previewUrl)
            ).get(
                AudioPlayerViewModel::class.java
            )
    }

    private fun observeViewModel() {
        viewModel.observeTimer().observe(this) {
            binding.currentTime.text = it
        }

        viewModel.observePlayerState().observe(this) {
            if (it == PlayerState.PLAYING) {
                binding.playButton.setImageResource(R.drawable.btn_pause)
            } else binding.playButton.setImageResource(
                R.drawable.btn_play
            )
        }
    }

    private fun setupClickListener() {
        binding.playButton.setOnClickListener {
            viewModel.onPlayClicked()
        }
    }

    private fun finishActivity() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun bindTrack() {
        val currentTrack = track
        if (currentTrack != null) {
            Glide.with(binding.trackImage)
                .load(getCoverArtwork(currentTrack))
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(R.dimen.big_track_image_corner_radius.dxToPx(this)))
                .into(binding.trackImage)

            binding.apply {
                trackName.text = currentTrack.trackName ?: getString(R.string.unknown_track)
                artistName.text = currentTrack.artistName ?: getString(R.string.unknown_artist)
                trackTime.text = currentTrack.trackTime?.let {
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(it)
                } ?: getString(R.string.unknown_time)
                trackAlbum.text = currentTrack.collectionName ?: getString(R.string.unknown_album)
                trackYear.text = getYearFromReleaseDate(currentTrack.releaseDate)
                trackGenre.text = currentTrack.primaryGenreName ?: getString(R.string.unknown_genre)
                trackCountry.text = currentTrack.country ?: getString(R.string.unknown_country)
            }
        }
    }

    private fun getCoverArtwork(track: Track) =
        track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

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


    companion object {
        const val KEY_TRACK = "track"
    }
}