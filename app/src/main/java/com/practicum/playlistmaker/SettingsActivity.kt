package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Завершение активити по кнопке назад
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Поделиться приложением
        val shareApp = findViewById<MaterialTextView>(R.id.share_app)
        shareApp.setOnClickListener {
            val shareAppIntent = Intent(Intent.ACTION_SEND).apply {
                setType("text/plain")
                putExtra(Intent.EXTRA_TEXT, getString(R.string.url_android_developer))
            }
            startActivity(Intent.createChooser(shareAppIntent, ""))
        }

        //Написать в поддержку
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

        //Открыть пользовательское соглашение
        val userAgreement = findViewById<MaterialTextView>(R.id.user_agreement)
        userAgreement.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_user_agreement)))
            startActivity(userAgreementIntent)
        }

    }
}