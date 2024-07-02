package com.bensek.mlkitdocumentscanner

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bensek.mlkitdocumentscanner.databinding.FragmentHomeBinding
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var scannerHandler: MLKitDocumentScannerManager
    private lateinit var imageListAdapter: ImageListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupScannerHandler()
        setupButtonClickListener()
        setupRecyclerView()
    }

    private fun setupScannerHandler() {
        scannerHandler = MLKitDocumentScannerManager()
        scannerHandler.register(this) { resultCode, data ->
            if (resultCode == Activity.RESULT_OK) {
                // Handle the result here
                val result = GmsDocumentScanningResult.fromActivityResultIntent(data)
                val bitmaps = result?.pages?.mapNotNull { page ->
                    convertToBitmap(page.imageUri)
                }
                bitmaps?.let {
                    imageListAdapter.setImages(it)
                }
            }
        }
    }

    private fun convertToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setupButtonClickListener() {
        binding.buttonFirst.setOnClickListener {
            scannerHandler.startSdkActivity(requireContext())
        }
    }

    private fun setupRecyclerView() {
        imageListAdapter = ImageListAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = imageListAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}