package com.bensek.mlkitdocumentscanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning

class MLKitDocumentScannerManager {
    private lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>

    fun register(fragment: Fragment, callback: (resultCode: Int, data: Intent?) -> Unit) {
        launcher = fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            callback(result.resultCode, result.data)
        }
    }

    fun startSdkActivity(context: Context) {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF,
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
            )
            .setPageLimit(20)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE)
            .build()

        val scanner = GmsDocumentScanning
            .getClient(options)

        scanner.getStartScanIntent(context as Activity)
            .addOnSuccessListener { intent ->
                val request = IntentSenderRequest.Builder(intent)
                    .build()

                launcher.launch(request)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    exception.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}