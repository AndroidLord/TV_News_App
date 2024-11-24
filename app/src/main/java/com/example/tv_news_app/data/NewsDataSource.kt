package com.example.tv_news_app.data

import com.example.tv_news_app.data.api.NewsApiService
import retrofit2.Retrofit
import javax.inject.Inject

class NewsDataSource @Inject constructor(
    retrofit: Retrofit
) {

    private val newsApiService: NewsApiService =
        retrofit.create(NewsApiService::class.java)

    suspend fun getCategoryNews(category: String, page: Int) = newsApiService.getCategoryHeadlineNews(
        countryCode = COUNTRY_CODE,
        category = category,
        pageSize = 100,
        page = page,
        apiKey = API_KEY2
    )

}