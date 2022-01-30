package com.mindfulness.googlenewsembed.ui.web_view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mindfulness.googlenewsembed.R
import android.webkit.WebChromeClient
import android.view.WindowManager
import android.os.Build
import android.view.View
import android.view.ViewGroup
import com.mindfulness.googlenewsembed.ui.web_view.VideoEnabledWebChromeClient.ToggledFullscreenCallback


class NewsWebViewActivity : AppCompatActivity() {

    private var myWebView: VideoEnabledWebView? = null
    private var webChromeClient: VideoEnabledWebChromeClient? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_web_view)

        val author = intent.getStringExtra("author")
        val urlNews = intent.getStringExtra("url")

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = author
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        myWebView = findViewById<VideoEnabledWebView>(R.id.web_view_news)
        myWebView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                view.loadUrl(url)
                return true
            }
        }

      /*  myWebView.loadUrl(urlNews!!)
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.allowContentAccess = true
        myWebView.settings.domStorageEnabled = true
        myWebView.settings.useWideViewPort = true
        myWebView.webChromeClient = WebChromeClient()
        myWebView.settings.allowFileAccess = true
        myWebView.settings.setAppCacheEnabled(true)
*/
        ///
        // Set layout
        // Set layout
       // setContentView(R.layout.activity_main)

        // Save the web view

        // Save the web view
        //var webView = findViewById<View>(R.id.webView) as VideoEnabledWebView

        // Initialize the VideoEnabledWebChromeClient and set event handlers

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        val nonVideoLayout: View =
            findViewById(R.id.nonVideoLayout) // Your own view, read class comments

        val videoLayout =
            findViewById<View>(R.id.videoLayout) as ViewGroup // Your own view, read class comments

        val loadingView: View = layoutInflater.inflate(
            R.layout.view_loading_video,
            null
        ) // Your own view, read class comments

        webChromeClient = object : VideoEnabledWebChromeClient(
            nonVideoLayout, videoLayout, loadingView, myWebView // See all available constructors...
        ) {
            // Subscribe to standard events, such as onProgressChanged()...
            override fun onProgressChanged(view: WebView, progress: Int) {
                // Your code...
            }
        }
        webChromeClient?.setOnToggledFullscreen(ToggledFullscreenCallback { fullscreen -> // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
            if (fullscreen) {
                val attrs = window.attributes
                attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                window.attributes = attrs
                if (Build.VERSION.SDK_INT >= 14) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                }
            } else {
                val attrs = window.attributes
                attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                window.attributes = attrs
                if (Build.VERSION.SDK_INT >= 14) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        })
        myWebView?.webChromeClient = webChromeClient

        // Navigate everywhere you want, this classes have only been tested on YouTube's mobile site

        // Navigate everywhere you want, this classes have only been tested on YouTube's mobile site
        myWebView?.loadUrl(urlNews!!)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient!!.onBackPressed()) {
            if (myWebView.let { it!!.canGoBack() }) {
                myWebView?.goBack()
            } else {
                // Close app (presumably)
                super.onBackPressed()
            }
        }
    }
}