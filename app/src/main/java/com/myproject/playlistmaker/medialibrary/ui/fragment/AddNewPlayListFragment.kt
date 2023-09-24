package com.myproject.playlistmaker.medialibrary.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.FragmentAddNewPlayListBinding
import com.myproject.playlistmaker.medialibrary.viewmodel.AddNewPlayListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddNewPlayListFragment : Fragment() {
    private lateinit var binding: FragmentAddNewPlayListBinding
    private val vm: AddNewPlayListViewModel by viewModel()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.uriLiveData.observe(viewLifecycleOwner) {
            imageUri = it
        }
        val confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle(requireContext().getString(R.string.complete_creating_of_playlist))
            .setMessage(requireContext().getString(R.string.unsaved_data_will_be_lost))
            .setNeutralButton(requireContext().getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(requireContext().getString(R.string.finish)) { _, _ ->
                findNavController().navigateUp()
            }

        binding.buttonBack.setOnClickListener {
            if (imageUri != null ||
                !binding.editTextPlaylistTitle.text.isNullOrEmpty() ||
                !binding.editTextPlaylistDescription.text.isNullOrEmpty()) {
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )

        val pickMedia1 =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    vm.saveToStorage(uri, requireContext().applicationContext)
                    binding.imageContainer.setImageURI(uri)
                    binding.placeholder.visibility = View.GONE
                }
            }


        binding.imageContainer.setOnClickListener{
            pickMedia1.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.editTextPlaylistTitle.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isNotEmpty()) {
                    binding.buttonCreatePlaylist.isEnabled = true
                    context?.let {
                        ContextCompat.getColor(
                            it,
                            R.color.vp_blue)
                    }?.let {
                        binding.buttonCreatePlaylist.setBackgroundColor(
                            it
                        )
                    }
                } else {
                    binding.buttonCreatePlaylist.isEnabled = false
                    context?.let {
                        ContextCompat.getColor(
                            it,
                            R.color.gray)
                    }?.let {
                        binding.buttonCreatePlaylist.setBackgroundColor(
                            it
                        )
                    }
                }
            }
        }

        binding.editTextPlaylistDescription.doOnTextChanged { text, _, _, _ ->

        }

        binding.buttonCreatePlaylist.setOnClickListener {
            vm.insertPlaylist(binding.editTextPlaylistTitle.text.toString(),
                imageUri,
                binding.editTextPlaylistDescription.text.toString()
            )

            imageUri?.let {
                vm.saveToStorage(it, requireContext().applicationContext)
            }

            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, binding.editTextPlaylistTitle.text.toString()),
                Toast.LENGTH_LONG
            ).show()

            findNavController().navigateUp()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() =
            AddNewPlayListFragment()
    }
}