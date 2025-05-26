package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.search.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Переход на экран поиска
        val searchButton = findViewById<Button>(R.id.btn_search)
        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        //Переход на экран медиатеки
        val mediaLibraryButton = findViewById<Button>(R.id.btn_media_library)
        mediaLibraryButton.setOnClickListener {
            val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
        }

        //Переход на экран настроек
        val settingsButton = findViewById<Button>(R.id.btn_settings)
        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}