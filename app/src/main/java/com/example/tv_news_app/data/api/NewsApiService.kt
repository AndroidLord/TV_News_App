package com.example.tv_news_app.data.api

import com.example.tv_news_app.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines")
    suspend fun getCategoryHeadlineNews(
        @Query("country") countryCode: String,
        @Query("category") category: String,
        @Query("pageSize") pageSize: Int,
        @Query("Page") page: Int,
        @Query("apiKey") apiKey: String
    ): NewsResponse


}