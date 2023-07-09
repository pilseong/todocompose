package net.pilseong.todocompose.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import net.pilseong.todocompose.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale


//@SuppressLint("MissingPermission")
//fun startRecordingVideo(
//    context: Context,
//    filenameFormat: String,
//    videoCapture: VideoCapture<Recorder>,
//    outputDirectory: File,
//    executor: Executor,
//    audioEnabled: Boolean,
//    consumer: Consumer<VideoRecordEvent>
//): Recording {
//    val videoFile = File(
//        outputDirectory,
//        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".mp4"
//    )
//
//    val outputOptions = FileOutputOptions.Builder(videoFile).build()
//
//    return videoCapture.output
//        .prepareRecording(context, outputOptions)
//        .apply { if (audioEnabled) withAudioEnabled() }
//        .start(executor, consumer)
//}


fun getOutputDirectory(context: Context): File {
    val mediaDir =
        File(context.filesDir, context.resources.getString(R.string.app_name)).apply { mkdirs() }

    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}

fun savePhotoToInternalStorage(uri: Uri, context: Context): Uri? {

    return try {
        val targetFile = File(
            getOutputDirectory(context),
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
                .format(System.currentTimeMillis()) + ".jpg"
        )
        copy(context, uri, targetFile)

        targetFile.toUri()
    } catch (e: Exception) {
        Log.d("PHILIP", "Can't save file $uri ")
        e.printStackTrace()
        null
    }
}

fun copy(context: Context, srcUri: Uri, dstFile: File?) {

    try {
        val inputStream = context.contentResolver.openInputStream(srcUri) ?: return

        inputStream.use {
            val outputStream: OutputStream = FileOutputStream(dstFile)

            outputStream.use {
                outputStream.write(inputStream.readBytes())

            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun deleteFileFromUri(uri: Uri) {
    val file = uri.toFile()
    if (file.exists()) {
        try {
            file.delete()
            Log.d("PHILIP", "$uri file deleted")
        } catch (e: Exception) {
            Log.d("PHILIP", "Can't remove file $uri ")
        }
    }
}