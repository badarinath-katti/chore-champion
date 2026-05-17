package com.chorechampion.app.util

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraManager(private val context: Context) {

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    suspend fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        executor: Executor
    ) {
        val cameraProvider = getCameraProvider()
        this.cameraProvider = cameraProvider

        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun takePhoto(): Uri = suspendCoroutine { continuation ->
        val imageCapture = imageCapture ?: run {
            continuation.resumeWithException(IllegalStateException("Camera not initialized"))
            return@suspendCoroutine
        }

        val photoFile = createPhotoFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    continuation.resume(Uri.fromFile(photoFile))
                }

                override fun onError(exception: ImageCaptureException) {
                    continuation.resumeWithException(exception)
                }
            }
        )
    }

    private fun createPhotoFile(): File {
        val photoDir = File(context.cacheDir, "chore_photos").apply {
            if (!exists()) mkdirs()
        }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(photoDir, "CHORE_${timestamp}.jpg")
    }

    private suspend fun getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(context).also { future ->
            future.addListener({
                continuation.resume(future.get())
            }, ContextCompat.getMainExecutor(context))
        }
    }

    fun shutdown() {
        cameraProvider?.unbindAll()
    }
}
