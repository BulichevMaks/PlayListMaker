package com.prakticum.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.Theme
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val movieBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(movieBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApi::class.java)
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var buttonBack: ImageView
    private lateinit var buttonRefresh: Button
    //   private lateinit var errorMessage: TextView

    private lateinit var placeholder: TextView

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)

    companion object {
        const val TEXT_CONTENTS = "TEXT_CONTENTS"
    }

    private var input = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        linearLayout = findViewById(R.id.container)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        buttonBack = findViewById(R.id.buttonBack)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        //     errorMessage = findViewById(R.id.errorMessage)

        placeholder = findViewById(R.id.placeholder)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val view: View? = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                tracks.clear()
                adapter.notifyDataSetChanged()
            }

        }
        buttonBack.setOnClickListener {
            finish()
        }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        buttonRefresh.setOnClickListener {
            search()
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                input = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)
    }

    private fun search() {
        trackService.search(input)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                showMessage("", "")
                            } else {
                                showMessage("Ничего не нашлось", "")
                            }

                        }
                        401 -> showMessage("You are not authorisation", response.code().toString())
                        else -> showMessage("Проблемы со связью", response.code().toString())
                    }

                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(
                        """
        Проблемы со связью
        
        Загрузка не удалась. Проверьте
        подключение к интернету
    """, t.message.toString()
                    )
                }

            })
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholder.visibility = View.VISIBLE
            //      errorMessage.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholder.text = text
            if (additionalMessage.isNotEmpty()) {
//                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
//                    .show()
                buttonRefresh.visibility = View.VISIBLE
                if (isDarkTheme()) {
                    placeholder.setDrawableTop(R.drawable.error_enternet_dark)

                } else {
                    placeholder.setDrawableTop(R.drawable.error_enternet_light)
                }
            }
        } else {
            placeholder.visibility = View.GONE
            buttonRefresh.visibility = View.GONE
        }
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        input = savedInstanceState.getString(TEXT_CONTENTS, "")
    }
}