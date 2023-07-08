package net.pilseong.todocompose.data.model.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import net.pilseong.todocompose.data.model.Photo

@Composable
fun rememberGalleryState(): GalleryState {
    return remember { GalleryState() }
}

class GalleryState {
    val images = mutableStateListOf<Photo>()
    val imagesToBeDeleted = mutableStateListOf<Photo>()

    fun addImage(galleryImage: Photo) {
        images.add(galleryImage)
    }

    fun removeImage(galleryImage: Photo) {
        images.remove(galleryImage)
        imagesToBeDeleted.add(galleryImage)
    }

    fun clearImagesTobeDeleted() {
        imagesToBeDeleted.clear()
    }
}

data class GalleryImage(
    val image: Uri,
    val remoteImagePath: String = ""
)