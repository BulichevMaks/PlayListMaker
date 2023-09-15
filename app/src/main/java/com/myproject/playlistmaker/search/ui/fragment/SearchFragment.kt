package com.myproject.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.FragmentSearchBinding
import com.myproject.playlistmaker.search.domain.model.Track
import com.myproject.playlistmaker.search.ui.models.SearchState
import com.myproject.playlistmaker.search.ui.viewmodel.SearchViewModel
import java.util.ArrayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val vm: SearchViewModel by viewModel()

    private lateinit var binding: FragmentSearchBinding

    private var tracks: ArrayList<Track> = ArrayList()
    private var historyTracks: ArrayList<Track> = ArrayList()
    private var trackAdapter = TrackAdapter(tracks)
    private var historyAdapter = HistoryAdapter(historyTracks)
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        vm.observeHistoryTracks().observe(viewLifecycleOwner) { tracks ->
            historyTracks.clear()
            historyTracks.addAll(tracks)
            historyAdapter.notifyDataSetChanged()
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            if (vm.isHistoryShouldShow()) {
                showHistory("")
            }
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
            vm.observeState().observe(viewLifecycleOwner) {
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
                startPlayer()
            }
        }

        inputTextHandle()
    }
    private fun startPlayer() {
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment)
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

        if ( s?.isEmpty() == true && historyTracks.isNotEmpty()) {
            binding.apply {
                placeholder.visibility = View.GONE
                buttonRefresh.visibility = View.GONE
                clearHistoryButton.visibility = View.VISIBLE
                youSearched.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                recyclerView.layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        true
                    )
                binding.recyclerView.adapter = historyAdapter
            }
        }

        historyAdapter.setOnItemClickListener { position ->
            vm.saveTrackToSharedPref(historyTracks, position)
            startPlayer()
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
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter

        binding.apply {
            progressBar.visibility = View.GONE
            placeholder.visibility = View.GONE
            buttonRefresh.visibility = View.GONE
            clearHistoryButton.visibility = View.VISIBLE
            youSearched.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun TextView.setDrawableTop(iconId: Int) {
        val icon = ContextCompat.getDrawable(context, iconId)
        this.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view: View? = requireActivity().currentFocus
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

}