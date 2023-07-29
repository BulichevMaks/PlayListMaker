package com.myproject.playlistmaker.settings.ui.fragment

import android.content.ActivityNotFoundException
import android.content.ComponentCallbacks
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.FragmentSettingsBinding
import com.myproject.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val vm: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.share.setOnClickListener {
            try {
                val message = getString(R.string.course_link)
                val sendIntent: Intent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(Intent.EXTRA_TEXT, message)
                    this.type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.application_missing),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.support.setOnClickListener {
            try {
                val message = getString(R.string.message_email)
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SENDTO
                    this.data = Uri.parse("mailto:")
                    this.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_theme))
                    this.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                    this.putExtra(Intent.EXTRA_TEXT, message)
                }
                startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.application_missing),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.userAgreement.setOnClickListener {
            try {
                val uri = Uri.parse(getString(R.string.practicum_offer_link))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.application_missing),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.themeSwitcher.isChecked = vm.isDarkOn()

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            vm.switchTheme(checked)
        }


        val configuration = resources.configuration
        var currentNightMode = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        requireContext().registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                val newNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode != newNightMode) {
                    currentNightMode = newNightMode
                    binding.themeSwitcher.isChecked =
                        currentNightMode == Configuration.UI_MODE_NIGHT_YES
                }
            }

            override fun onLowMemory() {}
        })
        binding.themeSwitcher.isChecked = vm.getCurrentTheme() == Configuration.UI_MODE_NIGHT_YES
    }

}
