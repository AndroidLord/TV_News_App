package com.example.tv_news_app.data

import com.example.tv_news_app.data.NewsDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsDataSource: NewsDataSource
) {
    suspend fun getCategoryNews(category: String, page: Int) = withContext(Dispatchers.IO) {
        newsDataSource.getCategoryNews(category, page)
    }
}