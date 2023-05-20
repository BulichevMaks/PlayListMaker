package com.myproject.playlistmaker.presentation.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.myproject.playlistmaker.PREFERENCES
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.creator.Creator
import com.myproject.playlistmaker.domain.models.Track

import com.myproject.playlistmaker.domain.models.TracksResult
import com.myproject.playlistmaker.domain.usecase.ItemClickUseCase
import com.myproject.playlistmaker.domain.usecase.ItemHistoryClickUseCase
import com.myproject.playlistmaker.domain.usecase.SearchTracksUseCase
import com.myproject.playlistmaker.presentation.Event
import com.myproject.playlistmaker.presentation.HistoryAdapter
import com.myproject.playlistmaker.presentation.TrackAdapter
import java.util.*
import kotlin.collections.ArrayList

const val LIST_KEY = "key_for_list"

class SearchActivity : AppCompatActivity() {
    private val trackRepository by lazy {
        Creator.getTrackRepository()
    }
    private val itemClickUseCase by lazy {
        ItemClickUseCase(trackRepository)
    }
    private val searchTracksUseCase by lazy {
        SearchTracksUseCase(trackRepository)
    }
    private val itemHistoryClickUseCase by lazy {
        ItemHistoryClickUseCase(trackRepository)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var buttonBack: ImageView
    private lateinit var buttonRefresh: Button
    private lateinit var placeholder: TextView
    private lateinit var text: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var nothingFound: String
    private lateinit var errorMessage: String
    private lateinit var progressBar: ProgressBar
    private var image = R.drawable.error_not_found_dark
    private var tracks: ArrayList<Track> = ArrayList()
    private var historyTracks: ArrayList<Track> = ArrayList()
    private val trackAdapter = TrackAdapter(tracks)
    private var historyAdapter = HistoryAdapter(historyTracks)
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        nothingFound = resources.getString(R.string.nothing_found)
        errorMessage = resources.getString(R.string.error_message)
        recyclerView = findViewById(R.id.recyclerView)
        linearLayout = findViewById(R.id.container)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        buttonBack = findViewById(R.id.buttonBack)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        placeholder = findViewById(R.id.placeholder)
        text = findViewById(R.id.text)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)
        placeholder.text = nothingFound
        val sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        historyTracks = readFromPref(sharedPreferences)!!
        historyAdapter = HistoryAdapter(historyTracks)

        savedInstanceState?.let {
            image = it.getInt(IMAGE)
            placeholder.setDrawableTop(image)
        }
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = trackAdapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val view: View? = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
            }
        }
        buttonBack.setOnClickListener {
            finish()
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            showHistory("")
        }
        buttonRefresh.setOnClickListener {
            search()
        }
        clearHistoryButton.setOnClickListener {
            historyTracks.clear()
            clearHistoryButton.visibility = View.GONE
            text.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
        }

        trackAdapter.setOnItemClickListener { position ->
            itemClickUseCase.execute(tracks, historyTracks, position)
            if (clickDebounce()) {
                startIntent()
            }
        }

        historyAdapter.setOnItemClickListener { position ->
            itemHistoryClickUseCase.execute(historyTracks, position)
            startIntent()
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                input = s.toString()
                searchDebounce()
                clearButton.visibility = clearButtonVisibility(s)
                showHistory(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        writeToPref(sharedPreferences, historyTracks)
    }

    private fun startIntent() {
        val intent = Intent(this, PlayerViewActivity::class.java)
        startActivity(intent)
    }

    fun showHistory(s: CharSequence?) {
        if (inputEditText.hasFocus() && s?.isEmpty() == true && historyTracks.isNotEmpty()
        ) {
            placeholder.visibility = View.GONE
            buttonRefresh.visibility = View.GONE
            recyclerView.adapter = historyAdapter
            clearHistoryButton.visibility = View.VISIBLE
            text.visibility = View.VISIBLE
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        } else {
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = trackAdapter
            clearHistoryButton.visibility = View.GONE
            text.visibility = View.GONE
        }
    }

    private fun search() {
        if (input.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholder.visibility = View.GONE
            searchTracksUseCase.execute(input) { result ->
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                when (result) {
                    is TracksResult.Success -> {
                        val tracks = result.tracks
                        if (tracks.isNotEmpty()) {
                            this.tracks.clear()
                            this.tracks.addAll(tracks)
                            trackAdapter.notifyDataSetChanged()
                            showMessage("", Event.SUCCESS)
                        }
                    }
                    is TracksResult.Error -> {
                        when (result.code) {
                            404 -> showMessage(nothingFound, Event.NOTHING_FOUND)
                            401 -> showMessage("You are not authorized", Event.ERROR)
                            500 -> showMessage(errorMessage, Event.SERVER_ERROR)
                            -1 -> showMessage(nothingFound, Event.NOTHING_FOUND)
                            else -> showMessage(errorMessage, Event.ERROR)
                        }
                    }
                }
            }
        }
    }

    private fun showMessage(text: String, event: Event) {
        runOnUiThread {
            placeholder = findViewById(R.id.placeholder)
            when (event) {
                Event.SUCCESS -> {
                    placeholder.visibility = View.GONE
                    buttonRefresh.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                Event.NOTHING_FOUND -> {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    placeholder.text = text
                    placeholder.visibility = View.VISIBLE
                    buttonRefresh.visibility = View.GONE
                    image = if (isDarkTheme()) {
                        placeholder.setDrawableTop(R.drawable.error_not_found_dark)
                        R.drawable.error_not_found_dark
                    } else {
                        placeholder.setDrawableTop(R.drawable.error_not_found_light)
                        R.drawable.error_not_found_light
                    }
                }
                Event.ERROR -> {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    placeholder.text = text
                }
                Event.SERVER_ERROR -> {
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                    placeholder.text = text
                    placeholder.visibility = View.VISIBLE
                    buttonRefresh.visibility = View.VISIBLE
                    image = if (isDarkTheme()) {
                        placeholder.setDrawableTop(R.drawable.error_enternet_dark)
                        R.drawable.error_enternet_dark

                    } else {
                        placeholder.setDrawableTop(R.drawable.error_enternet_light)
                        R.drawable.error_enternet_light
                    }
                }
            }
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_CONTENTS, input)
        outState.putInt(STATE_PLACEHOLDER_VISIBILITY, placeholder.visibility)
        outState.putInt(STATE_BUTTON_VISIBILITY, buttonRefresh.visibility)
        outState.putInt(IMAGE, image)
        outState.putString(ERROR_MESSAGE, placeholder.text.toString())
        if (tracks.isNotEmpty()) {
            outState.putParcelableArrayList(
                TRACK_LIST,
                tracks as ArrayList<out Parcelable?>?
            )
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        input = savedInstanceState.getString(TEXT_CONTENTS, "")
        image = savedInstanceState.getInt(IMAGE)
        placeholder.text = savedInstanceState.getString(ERROR_MESSAGE, "")
        placeholder.visibility =
            savedInstanceState.getInt(STATE_PLACEHOLDER_VISIBILITY, View.INVISIBLE)
        buttonRefresh.visibility =
            savedInstanceState.getInt(STATE_BUTTON_VISIBILITY, View.INVISIBLE)
        if (tracks.isNotEmpty()) {
            tracks.addAll(savedInstanceState.getParcelableArrayList<Parcelable>(TRACK_LIST) as ArrayList<Track>)
        }
    }

    private fun writeToPref(sharedPreferences: SharedPreferences, user: ArrayList<Track>) {
        val json = Gson().toJson(user)
        sharedPreferences.edit()
            .putString(LIST_KEY, json)
            .apply()
    }

    private fun readFromPref(sharedPreferences: SharedPreferences): ArrayList<Track>? {
        val json = sharedPreferences.getString(LIST_KEY, null) ?: return arrayListOf()
        return Gson().fromJson(json, Array<Track>::class.java)?.let { ArrayList(it.toList()) }
    }

    companion object {
        private var input = ""
        const val TEXT_CONTENTS = "TEXT_CONTENTS"
        const val TRACK_LIST = "TRACK_LIST"
        const val HISTORY_TRACK_LIST = "HISTORY_TRACK_LIST"
        const val STATE_PLACEHOLDER_VISIBILITY = "STATE_PLACEHOLDER_VISIBILITY"
        const val STATE_BUTTON_VISIBILITY = "STATE_BUTTON_VISIBILITY"
        const val ERROR_MESSAGE = "ERROR_MESSAGE"
        const val IMAGE = "IMAGE"
        const val SEL_ITEM_URL = "SEL_ITEM_URL"
        const val SEL_ITEM = "SEL_ITEM"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}