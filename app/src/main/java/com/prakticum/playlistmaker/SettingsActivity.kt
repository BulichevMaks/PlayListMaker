package com.prakticum.playlistmaker

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<ImageView>(R.id.buttonBack)
        val buttonShare = findViewById<TextView>(R.id.share)
        val buttonSupport = findViewById<TextView>(R.id.support)
        val buttonUserAgreement = findViewById<TextView>(R.id.user_agreement)

        buttonBack.setOnClickListener {
            finish()
        }
        buttonShare.setOnClickListener {
            val message = getString(R.string.course_link)
            val sendIntent: Intent = Intent().apply {
               this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, message)
                this.type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        buttonSupport.setOnClickListener {
            val message = getString(R.string.message_email)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.message_theme))
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }
        buttonUserAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.practicum_offer_link))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

    }
}