package com.myproject.playlistmaker.search.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.ActivitySearchBinding

import com.myproject.playlistmaker.player.ui.activity.PlayerActivity
import com.myproject.playlistmaker.search.domain.madel.Track
import com.myproject.playlistmaker.search.ui.models.SearchState

import com.myproject.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity() {

    private val vm: SearchViewModel by viewModel()

    private lateinit var binding: ActivitySearchBinding
    private var image = R.drawable.error_not_found_dark
    private var tracks: ArrayList<Track> = ArrayList()
    private var historyTracks: ArrayList<Track> = ArrayList()
    private val trackAdapter = TrackAdapter(tracks)
    private var historyAdapter = HistoryAdapter(historyTracks)
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {
            image = it.getInt(IMAGE)
            binding.placeholder.setDrawableTop(image)
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val view: View? = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
            }
        }
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            showHistory("")
        }
        binding.buttonRefresh.setOnClickListener {

            vm.searchDebounceRefresh(binding.inputEditText.text.toString())

            binding.apply {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                placeholder.visibility = View.GONE
                buttonRefresh.visibility = View.GONE
            }
            vm.observeState().observe(this) {
                render(it)
            }
        }
        binding.clearHistoryButton.setOnClickListener {
            historyTracks.clear()
            binding.clearHistoryButton.visibility = View.GONE
            binding.text.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()

        }

        trackAdapter.setOnItemClickListener { position ->
            vm.saveTracksToHistory(tracks, historyTracks, position)
            if (clickDebounce()) {
                startIntent()
            }
        }

        inputTextHandle()

        vm.observeHistoryTracks().observe(this) {
            historyTracks = it
            historyAdapter = HistoryAdapter(historyTracks)
        }
        vm.observeState().observe(this) {
            render(it)
        }
    }

    override fun onStop() {
        super.onStop()
        vm.writeTracksToPref(historyTracks)
    }

    private fun startIntent() {
        val intent = Intent(this, PlayerActivity::class.java)
        startActivity(intent)
    }

    private fun inputTextHandle() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                vm.searchDebounce(
                    changedText = s?.toString() ?: ""
                )

                binding.clearIcon.visibility = clearButtonVisibility(s)
                showHistory(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }
    }

    fun showHistory(s: CharSequence?) {

        vm.observeHistoryTracks().observe(this) { historyTracks ->
            if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && historyTracks.isNotEmpty()) {
                binding.apply {
                    placeholder.visibility = View.GONE
                    buttonRefresh.visibility = View.GONE
                    clearHistoryButton.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                    recyclerView.layoutManager =
                        LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, true)
                }
            } else {
                binding.apply {
                    recyclerView.layoutManager =
                        LinearLayoutManager(
                            this@SearchActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    clearHistoryButton.visibility = View.GONE
                    text.visibility = View.GONE
                }
            }

            val adapter = HistoryAdapter(historyTracks)
            adapter.setOnItemClickListener { position ->
                vm.saveTrackToSharedPref(historyTracks, position)
                startIntent()
            }

            binding.recyclerView.adapter = adapter
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.ServerError -> showServerError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholder.visibility = View.GONE
        }
    }

    private fun showError(errorMessage: String) {
        showMessage(errorMessage, Event.ERROR)
    }

    private fun showServerError(errorMessage: String) {
        showMessage(errorMessage, Event.SERVER_ERROR)
    }

    private fun showEmpty(emptyMessage: String) {
        showMessage(emptyMessage, Event.NOTHING_FOUND)
    }

    private fun showContent(tracks: List<Track>) {
        this.tracks.clear()
        this.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter
        showMessage("", Event.SUCCESS)
    }

    private fun showMessage(text: String, event: Event) {
        runOnUiThread {
            when (event) {
                Event.SUCCESS -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        placeholder.visibility = View.GONE
                        buttonRefresh.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
                Event.NOTHING_FOUND -> {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    binding.apply {
                        placeholder.text = text
                        progressBar.visibility = View.GONE
                        placeholder.visibility = View.VISIBLE
                        buttonRefresh.visibility = View.GONE
                    }
                    image = if (isDarkTheme()) {
                        binding.placeholder.setDrawableTop(R.drawable.error_not_found_dark)
                        R.drawable.error_not_found_dark
                    } else {
                        binding.placeholder.setDrawableTop(R.drawable.error_not_found_light)
                        R.drawable.error_not_found_light
                    }
                }
                Event.SERVER_ERROR -> {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    binding.placeholder.text = text
                    binding.progressBar.visibility = View.GONE
                }
                Event.ERROR -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        tracks.clear()
                        trackAdapter.notifyDataSetChanged()
                        placeholder.text = text
                        placeholder.visibility = View.VISIBLE
                        buttonRefresh.visibility = View.VISIBLE
                    }
                    image = if (isDarkTheme()) {
                        binding.placeholder.setDrawableTop(R.drawable.error_enternet_dark)
                        R.drawable.error_enternet_dark

                    } else {
                        binding.placeholder.setDrawableTop(R.drawable.error_enternet_light)
                        R.drawable.error_enternet_light
                    }
                }
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun isDarkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun TextView.setDrawableTop(iconId: Int) {
        val icon = this.context?.resources?.getDrawable(iconId)
        this.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        const val IMAGE = "IMAGE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}