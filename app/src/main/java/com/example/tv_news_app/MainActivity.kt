package com.example.tv_news_app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.example.tv_news_app.ui.theme.TV_News_AppTheme
import com.example.tv_news_app.ui.screen.NewsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TV_News_AppTheme {
                NewsScreen()
            }
        }
    }
}

