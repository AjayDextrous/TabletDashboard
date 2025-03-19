package com.example.tabletdashboard.ui.widgets

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebviewWidget(
    modifier: Modifier = Modifier,
    url: String? = null,
    html: String? = null
) {
    // Choose either the URL or HTML content to display
    val content = url ?: html

    // Ensure content is provided
    requireNotNull(content) { "You must provide either a URL or HTML content" }

    Box(modifier = modifier.fillMaxSize().clip(shape = RoundedCornerShape(8.dp))) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient() // Ensures links open inside WebView instead of external browser

                    if (url != null) {
                        loadUrl(url) // Load URL if provided
                    } else if (html != null) {
                        loadDataWithBaseURL(null, html, "text/html", "UTF-8", null) // Load HTML if provided
                    }
                }
            },
            update = { webView ->
                // Update the WebView when needed (optional)
                if (url != null) {
                    webView.loadUrl(url)
                } else if (html != null) {
                    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                }
            }
        )
    }
}

