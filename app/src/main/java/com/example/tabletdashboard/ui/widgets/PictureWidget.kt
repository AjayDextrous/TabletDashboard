package com.example.tabletdashboard.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.tabletdashboard.ui.theme.TabletDashboardTheme

@Composable
fun PictureWidget(
    modifier: Modifier = Modifier
) {
    // State to store the width and height of the composable
    val size = remember { mutableStateOf(IntSize(100, 100)) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .onSizeChanged { size.value = it },
        contentAlignment = Alignment.Center
    ) {
        // Load the image with the dynamic URL based on width and height
        val imageUrl = "https://picsum.photos/${size.value.width}/${size.value.height}"
        val displayText = remember { mutableStateOf("???") }

        // Display the image
        AsyncImage(
            model = imageUrl,
            contentDescription = "random image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onState = { state ->
                when (state) {
                    AsyncImagePainter.State.Empty -> displayText.value = "Empty"
                    is AsyncImagePainter.State.Error -> {
                        displayText.value = "Error ${state.result.throwable}"
                    }

                    is AsyncImagePainter.State.Loading -> displayText.value = "Loading"
                    is AsyncImagePainter.State.Success -> displayText.value = ""
                }
            }
        )

        BasicText(displayText.value)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TabletDashboardTheme {
        PictureWidget()
    }
}
