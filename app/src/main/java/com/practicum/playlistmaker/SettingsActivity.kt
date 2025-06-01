package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        finishActivity()
        shareApp()
        writeToSupport()
        openUserAgreement()
        setupThemeSwitcher()
    }

    private fun finishActivity() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun shareApp() {
        val shareApp = findViewById<MaterialTextView>(R.id.share_app)
        shareApp.setOnClickListener {
            val shareAppIntent = Intent(Intent.ACTION_SEND).apply {
                setType("text/plain")
                putExtra(Intent.EXTRA_TEXT, getString(R.string.url_android_developer))
            }
            startActivity(Intent.createChooser(shareAppIntent, ""))
        }
    }

    private fun writeToSupport() {
        val writeToSupport = findViewById<MaterialTextView>(R.id.write_to_support)
        writeToSupport.setOnClickListener {
            val writeToSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_address)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            }
            startActivity(writeToSupportIntent)
        }
    }

    private fun openUserAgreement() {
        val userAgreement = findViewById<MaterialTextView>(R.id.user_agreement)
        userAgreement.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_user_agreement)))
            startActivity(userAgreementIntent)
        }
    }

    private fun setupThemeSwitcher() {
        val app = applicationContext as App
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            app.switchTheme(isChecked)
        }

        themeSwitcher.isChecked = app.darkTheme
    }
}