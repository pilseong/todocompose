package net.pilseong.todocompose.ui.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import net.pilseong.todocompose.data.model.Photo
import net.pilseong.todocompose.ui.theme.SMALL_PADDING
import kotlin.math.max

@Composable
fun ComposeGallery(
    isEditMode: Boolean = false,
    photos: List<Photo>,
    modifier: Modifier = Modifier,
    imageSize: Dp = 45.dp,
    spaceBetween: Dp = SMALL_PADDING,
    imageShape: CornerBasedShape = Shapes().extraSmall,
    onAddClicked: () -> Unit = {},
    onImageClicked: (Photo) -> Unit = {},
    onCameraClicked: () -> Unit = {},
    onImagesSelected: (List<Uri>) -> Unit = {},
) {

    val focusManager = LocalFocusManager.current

    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
    ) { images -> onImagesSelected(images) }

    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = this.maxWidth.div(spaceBetween + imageSize).toInt().minus(if (isEditMode) 3 else 0)
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                photos.size - numberOfVisibleImages.value
            }
        }
        Log.d(
            "PHILIP",
            "numberOfVisibleImages ${numberOfVisibleImages.value}, ${remainingImages.value}"
        )

        Row {
            if (isEditMode) {
                Surface(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(shape = imageShape),
                    onClick = {
                        focusManager.clearFocus()
                        onCameraClicked()
                    },
                    shape = RoundedCornerShape(4.dp),
                    tonalElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "take pictures"
                        )
                    }
                }

                Spacer(modifier = Modifier.width(spaceBetween))

                AddImageButton(
                    imageSize = imageSize,
                    imageShape = imageShape,
                    onClick = {
                        focusManager.clearFocus()
                        onAddClicked()
                        multiplePhotoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    })

                Spacer(modifier = Modifier.width(spaceBetween))
            }
            LazyRow {
                items(
                    photos.toList(),
                    key = { photo -> photo.uri }
                ) { galleryImage ->
                    Log.d("PHILIP", "Inside lazy load $galleryImage")
                    AsyncImage(
                        modifier = Modifier
                            .clip(imageShape)
                            .size(imageSize)
                            .clickable {
                                focusManager.clearFocus()
                                onImageClicked(galleryImage)
                            },
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(galleryImage.uri.toUri())
                            .crossfade(true)
                            .build(),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Gallery Image"
                    )
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                }
            }
        }
    }
}

@Composable
fun AddImageButton(
    imageSize: Dp,
    imageShape: CornerBasedShape,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(imageSize)
            .clip(shape = imageShape),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Icon",
            )
        }
    }
}

@Composable
fun ZoomableImage(
    isEditMode: Boolean = false,
    fromCamera: Boolean = false,
    selectedGalleryImage: Photo?,
    onCloseClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onCameraClick: () -> Unit,
    onUseClicked: () -> Unit,
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = maxOf(1f, minOf(scale * zoom, 5f))
                    val maxX = (size.width * (scale - 1)) / 2
                    val minX = -maxX
                    offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
                    val maxY = (size.height * (scale - 1)) / 2
                    val minY = -maxY
                    offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                }
            }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = maxOf(.5f, minOf(3f, scale)),
                    scaleY = maxOf(.5f, minOf(3f, scale)),
                    translationX = offsetX,
                    translationY = offsetY
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(selectedGalleryImage?.uri)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Fit,
            contentDescription = "Gallery Image"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (fromCamera) {
                Button(onClick = onCameraClick) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "PhotoCamera Icon"
                    )
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                    Text(text = "Camera")
                }
                Button(onClick = onUseClicked) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Use Icon")
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                    Text(text = "Use")
                }
            } else {
                Button(onClick = onCloseClicked) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                    Spacer(modifier = Modifier.width(SMALL_PADDING))
                    Text(text = "Close")
                }

                if (isEditMode) {
                    Button(onClick = onDeleteClicked) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
                        Spacer(modifier = Modifier.width(SMALL_PADDING))
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}

//@Composable
//fun LastImageOverlay(
//    imageSize: Dp,
//    imageShape: CornerBasedShape,
//    remainingImages: Int
//) {
//    Box(contentAlignment = Alignment.Center) {
//        Surface(
//            modifier = Modifier
//                .clip(imageShape)
//                .size(imageSize),
//            color = MaterialTheme.colorScheme.primaryContainer
//        ) {}
//        Text(
//            text = "+$remainingImages",
//            style = TextStyle(
//                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//                fontWeight = FontWeight.Medium
//            ),
//            color = MaterialTheme.colorScheme.onPrimaryContainer
//        )
//    }
//}
