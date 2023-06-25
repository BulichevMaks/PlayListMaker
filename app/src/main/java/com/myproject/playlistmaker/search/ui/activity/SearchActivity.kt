package com.myproject.playlistmaker.search.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.ActivitySearchBinding

import com.myproject.playlistmaker.player.ui.activity.PlayerViewActivity
import com.myproject.playlistmaker.search.domain.madel.Track
import com.myproject.playlistmaker.search.ui.models.SearchState

import com.myproject.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.myproject.playlistmaker.search.ui.viewmodel.SearchViewModelFactory
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity() {

    private lateinit var vm: SearchViewModel

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

        vm = ViewModelProvider(this, SearchViewModelFactory())[SearchViewModel::class.java]

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

                Log.d("LATEST", binding.inputEditText.text.toString())
                vm.searchDebounceRefresh(binding.inputEditText.text.toString())


            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.placeholder.visibility = View.GONE
            binding.buttonRefresh.visibility = View.GONE
            vm.observeState().observe(this) {
                Log.d("LATEST1", "$it")
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
            Log.d("trackAdapter", "trackAdapter")
            vm.saveTracksToHistory(tracks, historyTracks, position)
            if (clickDebounce()) {
                startIntent()
            }
        }

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
        val intent = Intent(this, PlayerViewActivity::class.java)
        startActivity(intent)
    }

    fun showHistory(s: CharSequence?) {

        vm.observeHistoryTracks().observe(this) { historyTracks ->
            if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && historyTracks.isNotEmpty()) {
                binding.placeholder.visibility = View.GONE
                binding.buttonRefresh.visibility = View.GONE
                binding.clearHistoryButton.visibility = View.VISIBLE
                binding.text.visibility = View.VISIBLE
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
            } else {
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.clearHistoryButton.visibility = View.GONE
                binding.text.visibility = View.GONE
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
        Log.d("ERRRRROR", "$state")
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.ServerError -> showServerError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.placeholder.visibility = View.GONE
    }

    private fun showError(errorMessage: String) {
       // Log.d("ERRRRROR", "$errorMessage")
        showMessage(errorMessage, Event.ERROR)
    }

    private fun showServerError(errorMessage: String) {
        showMessage(errorMessage, Event.SERVER_ERROR)
    }

    private fun showEmpty(emptyMessage: String) {
        showMessage(emptyMessage, Event.NOTHING_FOUND)
    }

    private fun showContent(tracks: List<Track>) {
        Log.d("DDD", "Items: $tracks")
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
                    binding.progressBar.visibility = View.GONE
                    binding.placeholder.visibility = View.GONE
                    binding.buttonRefresh.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
                Event.NOTHING_FOUND -> {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    binding.placeholder.text = text
                    binding.progressBar.visibility = View.GONE
                    binding.placeholder.visibility = View.VISIBLE
                    binding.buttonRefresh.visibility = View.GONE
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
                    Log.d("ERRRRROR", "$text")
                    binding.placeholder.text = text
                    binding.progressBar.visibility = View.GONE
                }
                Event.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    binding.placeholder.text = text
                    binding.placeholder.visibility = View.VISIBLE
                    binding.buttonRefresh.visibility = View.VISIBLE
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

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(TEXT_CONTENTS, input)
//        outState.putInt(STATE_PLACEHOLDER_VISIBILITY, binding.placeholder.visibility)
//        outState.putInt(STATE_BUTTON_VISIBILITY, binding.buttonRefresh.visibility)
//        outState.putInt(IMAGE, image)
//        outState.putString(ERROR_MESSAGE, binding.placeholder.text.toString())
//        if (tracks.isNotEmpty()) {
//            outState.putParcelableArrayList(
//                TRACK_LIST,
//                tracks as ArrayList<out Parcelable?>?
//            )
//        }
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        input = savedInstanceState.getString(TEXT_CONTENTS, "")
//        image = savedInstanceState.getInt(IMAGE)
//        binding.placeholder.text = savedInstanceState.getString(ERROR_MESSAGE, "")
//        binding.placeholder.visibility =
//            savedInstanceState.getInt(STATE_PLACEHOLDER_VISIBILITY, View.INVISIBLE)
//        binding.buttonRefresh.visibility =
//            savedInstanceState.getInt(STATE_BUTTON_VISIBILITY, View.INVISIBLE)
//        if (tracks.isNotEmpty()) {
//            tracks.addAll(savedInstanceState.getParcelableArrayList<Parcelable>(TRACK_LIST) as ArrayList<Track>)
//        }
//    }

//    private fun writeToPref(sharedPreferences: SharedPreferences, user: ArrayList<Track>) {
//        val json = Gson().toJson(user)
//        sharedPreferences.edit()
//            .putString(LIST_KEY, json)
//            .apply()
//    }

//    private fun readFromPref(sharedPreferences: SharedPreferences): ArrayList<Track>? {
//        val json = sharedPreferences.getString(LIST_KEY, null) ?: return arrayListOf()
//        return Gson().fromJson(json, Array<Track>::class.java)?.let { ArrayList(it.toList()) }
//    }

    companion object {
        // private var input = ""
        const val TEXT_CONTENTS = "TEXT_CONTENTS"
        const val TRACK_LIST = "TRACK_LIST"
        const val STATE_PLACEHOLDER_VISIBILITY = "STATE_PLACEHOLDER_VISIBILITY"
        const val STATE_BUTTON_VISIBILITY = "STATE_BUTTON_VISIBILITY"
        const val ERROR_MESSAGE = "ERROR_MESSAGE"
        const val IMAGE = "IMAGE"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}