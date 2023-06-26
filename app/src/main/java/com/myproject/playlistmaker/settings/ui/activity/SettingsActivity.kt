package com.myproject.playlistmaker.settings.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.ActivitySettingsBinding
import com.myproject.playlistmaker.settings.ui.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var vm: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this, SettingsViewModel.getViewModelFactory(application as App))[SettingsViewModel::class.java]

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.share.setOnClickListener {
            val message = getString(R.string.course_link)
            val sendIntent: Intent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, message)
                this.type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.support.setOnClickListener {
            val message = getString(R.string.message_email)
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SENDTO
                this.data = Uri.parse("mailto:")
                this.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_theme))
                this.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                this.putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(shareIntent)
        }

        binding.userAgreement.setOnClickListener {
            val uri = Uri.parse(getString(R.string.practicum_offer_link))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        binding.themeSwitcher.isChecked = vm.isDarkOn()

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            vm.switchTheme(checked)

        }
    }

}