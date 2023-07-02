package com.myproject.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.ActivitySearchBinding

import com.myproject.playlistmaker.player.ui.activity.PlayerActivity
import com.myproject.playlistmaker.search.domain.madel.Track
import com.myproject.playlistmaker.search.ui.models.SearchState

import com.myproject.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : AppCompatActivity() {

    private val vm: SearchViewModel by viewModel()

    private lateinit var binding: ActivitySearchBinding

    private var tracks: ArrayList<Track> = ArrayList()
    private var historyTracks: ArrayList<Track> = ArrayList()
    private var trackAdapter = TrackAdapter(tracks)
    private var historyAdapter = HistoryAdapter(historyTracks)

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm.observeState().observe(this) { state ->
            render(state)
        }

        vm.observeHistoryTracks().observe(this) { tracks ->
            historyTracks.clear()
            historyTracks.addAll(tracks)
            historyAdapter.notifyDataSetChanged()
        }

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
            if (vm.isHistoryShouldShow()) {
                showHistory("")
            }
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
            vm.clearHistory()
            binding.clearHistoryButton.visibility = View.GONE
            binding.youSearched.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
        }

        trackAdapter.setOnItemClickListener { position ->
            vm.saveTracksToHistory(tracks, historyTracks, position)
            if (clickDebounce()) {
                startIntent()
            }
        }

        inputTextHandle()

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
                if (vm.isHistoryShouldShow()) {
                    showHistory(s)
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }
    }

    fun showHistory(s: CharSequence?) {

        if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && tracks.isNotEmpty()) {
            binding.apply {
                placeholder.visibility = View.GONE
                buttonRefresh.visibility = View.GONE
                clearHistoryButton.visibility = View.VISIBLE
                youSearched.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                recyclerView.layoutManager =
                    LinearLayoutManager(
                        this@SearchActivity,
                        LinearLayoutManager.VERTICAL,
                        true
                    )
                binding.recyclerView.adapter = historyAdapter
            }
        }

        historyAdapter.setOnItemClickListener { position ->
            vm.saveTrackToSharedPref(historyTracks, position)
            startIntent()
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
        hideKeyboard()
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholder.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            youSearched.visibility = View.GONE
        }
    }

    private fun showError(errorMessage: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            placeholder.text = errorMessage
            placeholder.visibility = View.VISIBLE
            buttonRefresh.visibility = View.VISIBLE
            placeholder.setDrawableTop(R.drawable.error_enternet)
        }
    }

    private fun showServerError(errorMessage: String) {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
        binding.placeholder.text = errorMessage
        binding.progressBar.visibility = View.GONE
    }

    private fun showEmpty(emptyMessage: String) {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
        binding.apply {
            placeholder.text = emptyMessage
            progressBar.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
            buttonRefresh.visibility = View.GONE
            placeholder.setDrawableTop(R.drawable.error_not_found)
        }
    }

    private fun showContent(tracks: List<Track>) {
        this.tracks.clear()
        this.tracks.addAll(tracks)
        historyAdapter.notifyDataSetChanged()

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter

        binding.apply {
            progressBar.visibility = View.GONE
            placeholder.visibility = View.GONE
            buttonRefresh.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            youSearched.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
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

    private fun TextView.setDrawableTop(iconId: Int) {
        val icon = ContextCompat.getDrawable(context, iconId)
        this.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view: View? = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}