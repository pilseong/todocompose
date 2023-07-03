package net.pilseong.todocompose.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.util.Consumer
import net.pilseong.todocompose.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor


@SuppressLint("MissingPermission")
fun startRecordingVideo(
    context: Context,
    filenameFormat: String,
    videoCapture: VideoCapture<Recorder>,
    outputDirectory: File,
    executor: Executor,
    audioEnabled: Boolean,
    consumer: Consumer<VideoRecordEvent>
): Recording {
    val videoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".mp4"
    )

    val outputOptions = FileOutputOptions.Builder(videoFile).build()

    return videoCapture.output
        .prepareRecording(context, outputOptions)
        .apply { if (audioEnabled) withAudioEnabled() }
        .start(executor, consumer)
}


fun getOutputDirectory(content: Context): File {
    val mediaDir = content.externalMediaDirs.firstOrNull()?.let {
        File(it, content.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    return if (mediaDir != null && mediaDir.exists()) mediaDir else content.filesDir
}

//@OptIn(ExperimentalPermissionsApi::class)
//fun requestCameraPermission(cameraPermissionState: PermissionState) {
//    if (cameraPermissionState.status.isGranted) {
//
//    } else {
//
//    }
//    when {
//        ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED -> {
//            Log.i("kilo", "Permission previously granted")
//            shouldShowCamera.value = true
//        }
//
//        ActivityCompat.shouldShowRequestPermissionRationale(
//            this,
//            Manifest.permission.CAMERA
//        ) -> Log.i("kilo", "Show camera permissions dialog")
//
//        else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//    }
//}