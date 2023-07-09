package net.pilseong.todocompose.ui.screen.task

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import net.pilseong.todocompose.MainActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Composable
fun CameraView(
    context: Context,
    outputDirectory: File,
    onImageCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = PreviewView(context)
    var cameraSelector: CameraSelector
    lateinit var imageCapture: ImageCapture
    var cameraProvider: ProcessCameraProvider? = null

    DisposableEffect(Unit) {
        Log.d("PHILIP", "[CameraView] camera initialized")
        scope.launch {
            cameraProvider = context.getCameraProvider()
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            imageCapture = ImageCapture.Builder().build()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            try {
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("PHILIP", "Usecase binding failed", e)
            }
        }
        onDispose {
            cameraProvider?.unbindAll()
        }
    }


    Surface {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                previewView
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp, top = 24.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                Text(text = "Close")
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                modifier = Modifier
                    .padding(bottom = 30.dp),
                onClick = {
                    Log.i("PHILIP", "ON CLICK")
                    takePhoto(
                        outputDirectory = outputDirectory,
                        filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                        executor = MainActivity.getExecutor(context),
                        imageCapture = imageCapture,
                        onImageCaptured = onImageCaptured,
                    )
                },
                content = {
                    Icon(
                        imageVector = Icons.Sharp.Lens,
                        contentDescription = "Take picture",
                        tint = Color.White,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(1.dp)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            )
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

private fun takePhoto(
    outputDirectory: File,
    filenameFormat: String,
    executor: Executor,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
) {

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(
            filenameFormat,
            Locale.getDefault()
        ).format(System.currentTimeMillis()) + ".jpg"
    )

    imageCapture.takePicture(
        ImageCapture.OutputFileOptions.Builder(photoFile).build(), executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("PHILIP", "Take photo error:", exception)
//                onError(exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri!!
                Log.d("PHILIP", "Take photo successful $savedUri")
                onImageCaptured(savedUri)
            }
        })
}




//fun takePhoto(
//    context: Context,
//    filenameFormat: String,
//    imageCapture: ImageCapture,
//    onImageCaptured: (Uri) -> Unit,
//) {
////    val photoFile = File(
////        getOutputDirectory(context),
////        SimpleDateFormat(
////            filenameFormat,
////            Locale.getDefault()
////        ).format(System.currentTimeMillis()) + ".jpg"
////    )
//
//    val name =
//        SimpleDateFormat(filenameFormat, Locale.getDefault()).format(System.currentTimeMillis())
//    val contentValues = ContentValues().apply {
//        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraX-Image")
//        }
//    }
//
//    val outputOptions = ImageCapture.OutputFileOptions
//        .Builder(
//            context.contentResolver,
//            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
//            contentValues
//        ).build()
//
//    imageCapture.takePicture(
//        outputOptions,
//        MainActivity.getExecutor(context),
//        object : ImageCapture.OnImageSavedCallback {
//            override fun onError(exception: ImageCaptureException) {
//                Log.e("PHILIP", "Take photo error:", exception)
//                onError(exception)
//            }
//
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                val savedUri = outputFileResults.savedUri!!
//                Log.d("PHILIP", "Take photo successful $savedUri")
//                onImageCaptured(savedUri)
//            }
//        })
//}




//@Composable
//fun CameraView(
//    outputDirectory: File,
//    executor: Executor,
//    onImageCaptured: (Uri) -> Unit,
//    onError: (ImageCaptureException) -> Unit,
//    onDismiss: () -> Unit,
//) {
//    val lensFacing = CameraSelector.LENS_FACING_BACK
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    val preview = Preview.Builder().build()
//    val previewView = remember { PreviewView(context) }
//    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
//    val cameraSelector = CameraSelector.Builder()
//        .requireLensFacing(lensFacing)
//        .build()
//
//    LaunchedEffect(lensFacing) {
//        Log.d("PHILIP", "[CameraView] binding to lifecycle")
//        val cameraProvider = context.getCameraProvider()
//        cameraProvider.unbindAll()
//        cameraProvider.bindToLifecycle(
//            lifecycleOwner,
//            cameraSelector,
//            preview,
//            imageCapture
//        )
//
//        preview.setSurfaceProvider(previewView.surfaceProvider)
//    }
//
//
//    Surface {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = {
//                previewView
//            }
//        )
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(end = 24.dp, top = 24.dp),
//            horizontalArrangement = Arrangement.End,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Button(onClick = onDismiss) {
//                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
//                Text(text = "Close")
//            }
//        }
//        Row(
//            modifier = Modifier.fillMaxSize(),
//            verticalAlignment = Alignment.Bottom,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            IconButton(
//                modifier = Modifier
//                    .padding(bottom = 30.dp),
//                onClick = {
//                    Log.i("kilo", "ON CLICK")
////                    takePhoto(
////                        context = context,
////                        filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
////                        imageCapture = imageCapture,
////                        outputDirectory = outputDirectory,
////                        executor = executor,
////                        onImageCaptured = onImageCaptured,
////                        onError = onError
////                    )
//                },
//                content = {
//                    Icon(
//                        imageVector = Icons.Sharp.Lens,
//                        contentDescription = "Take picture",
//                        tint = Color.White,
//                        modifier = Modifier
//                            .size(100.dp)
//                            .padding(1.dp)
//                            .border(1.dp, Color.White, CircleShape)
//                    )
//                }
//            )
//        }
//    }
//}
