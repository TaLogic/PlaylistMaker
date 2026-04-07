package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsInteractor = Creator.provideSettingInteractor()
        val sharingInteractor = Creator.provideSharingInteractor()
        settingsViewModel = ViewModelProvider(this, SettingsViewModel.getFactory(sharingInteractor,settingsInteractor)).get(
            SettingsViewModel::class.java)

        finishActivity()
        onShareAppClicked()
        onWriteToSupportClicked()
        onUserAgreementClicked()
        setupThemeSwitcher()
    }

    private fun finishActivity() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun onShareAppClicked() {
        val shareApp = findViewById<MaterialTextView>(R.id.share_app)
        shareApp.setOnClickListener {
            settingsViewModel.shareApp()
        }
    }

    private fun onWriteToSupportClicked() {
        val writeToSupport = findViewById<MaterialTextView>(R.id.write_to_support)
        writeToSupport.setOnClickListener {
            settingsViewModel.writeToSupport()
        }
    }

    private fun onUserAgreementClicked() {
        val userAgreement = findViewById<MaterialTextView>(R.id.user_agreement)
        userAgreement.setOnClickListener {
            settingsViewModel.openUserAgreement()
        }
    }
    private fun setupThemeSwitcher() {
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        settingsViewModel.observeIsDarkThemeEnabled().observe(this) {
            themeSwitcher.isChecked = it

        }

        themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            settingsViewModel.updateThemeSetting(isChecked)
        }

    }

}