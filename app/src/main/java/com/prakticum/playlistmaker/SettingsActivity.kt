package com.prakticum.playlistmaker

import android.annotation.SuppressLint
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
            val message = "https://practicum.yandex.ru/profile/android-developer/"
            val sendIntent: Intent = Intent().apply {
               this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, message)
                this.type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        buttonSupport.setOnClickListener {
            val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Сообщение разработчикам и разработчицам приложения Playlist Maker")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("maxbulmail@yandex.ru"))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }
        buttonUserAgreement.setOnClickListener {
            val url = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

    }
}