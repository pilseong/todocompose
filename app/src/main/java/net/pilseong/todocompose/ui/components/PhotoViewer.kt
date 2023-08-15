package net.pilseong.todocompose.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.data.model.ui.MemoWithNotebook

@Composable
@OptIn(ExperimentalPagerApi::class)
fun PhotoViewer(
    photoOpen: Boolean,
    selectedGalleryImage: Photo?,
    task: MemoWithNotebook,
    onDismiss: () -> Unit,
) {
    if (photoOpen && selectedGalleryImage != null) {
        Log.d("PHILIP", "photoOpen $photoOpen, selectedImage $selectedGalleryImage")
        Dialog(
            onDismissRequest = {
                onDismiss()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {

            Surface(
                color = Color.Black
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        count = task.photos.size,
                        state = rememberPagerState(
                            initialPage = task.photos.indexOf(
                                selectedGalleryImage
                            )
                        ),
                        key = { task.photos[it].id }
                    ) { index ->
                        ZoomableImage(
                            selectedGalleryImage = task.photos[index],
                            onCloseClicked = {
                                onDismiss()
                                Log.d(
                                    "PHILIP",
                                    "INSIDE close clicked - photoOpen $photoOpen, selectedImage $selectedGalleryImage"
                                )
                            },
                            onDeleteClicked = { },
                            onCameraClick = {},
                            onUseClicked = {}
                        )
                    }
                }
            }
        }
    }
}