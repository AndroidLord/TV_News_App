package com.example.tv_news_app.ui.screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tv_news_app.data.NewsRepository
import com.example.tv_news_app.data.model.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.math.pow


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val newsRepository: NewsRepository
) : ViewModel() {

    companion object {
        private const val REFRESH_KEY = "refresh_trigger"
        private const val CATEGORY_KEY = "category"
        private const val PAGE_KEY = "page"
        const val TAG = "NewsViewModel"
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val refreshTrigger = savedStateHandle.getStateFlow(REFRESH_KEY, 0)
    private val pageState = savedStateHandle.getStateFlow(PAGE_KEY, 1)

    private val selectedCategory = savedStateHandle.getStateFlow(
        CATEGORY_KEY,
        NewsCategory.HEADLINES
    )

    val uiState: StateFlow<NewsUIState> = combine(
        refreshTrigger,
        selectedCategory
    ) { _, category ->
        val savedNews = savedStateHandle.get<NewsResponse>(selectedCategory.value.value)
        if (savedNews != null && !_isRefreshing.value) {
            Log.d(TAG, "Fetched saved news: ${category.value}")
            NewsUIState.Success(savedNews)
        } else {
            try {
                val result = newsRepository.getCategoryNews(
                    category = category.value,
                    page = pageState.value
                )
                Log.d(TAG, "Fetched category news: ${category.value}")
                _isRefreshing.value = false
                if (result.totalResults == 0 || result.articles.isEmpty()) {
                    NewsUIState.Error(
                        type = NewsUIState.Error.ErrorType.EMPTY
                    )
                } else {
                    savedStateHandle[selectedCategory.value.value] = result
                    NewsUIState.Success(result)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching news", e)
                NewsUIState.Error(
                    type = when (e) {
                        is IOException -> {
                            NewsUIState.Error.ErrorType.NETWORK
                        }

                        is HttpException -> {
                            NewsUIState.Error.ErrorType.SERVER
                        }

                        else -> {
                            NewsUIState.Error.ErrorType.UNKNOWN
                        }
                    }
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NewsUIState.Loading
        )

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            savedStateHandle[PAGE_KEY] = pageState.value
            savedStateHandle[REFRESH_KEY] = refreshTrigger.value + 1
            savedStateHandle.remove<NewsResponse>(selectedCategory.value.value)
        }
    }

    fun setCategory(category: NewsCategory) {
        viewModelScope.launch {
            if (selectedCategory.value != category) {
                savedStateHandle[CATEGORY_KEY] = category
                savedStateHandle[PAGE_KEY] = pageState.value
                savedStateHandle[REFRESH_KEY] = refreshTrigger.value + 1
            }
        }
    }

}

sealed class NewsUIState {
    data object Loading : NewsUIState()
    data class Success(val data: NewsResponse) : NewsUIState()
    data class Error(
        val type: ErrorType
    ) : NewsUIState() {
        enum class ErrorType {
            NETWORK,
            SERVER,
            UNKNOWN,
            EMPTY
        }
    }
}

enum class NewsCategory(val value: String, val displayName: String) {
    HEADLINES("general", "Headlines"),
    BUSINESS("business", "Business"),
    TECHNOLOGY("technology", "Technology"),
    ENTERTAINMENT("entertainment", "Entertainment"),
    HEALTH("health", "Health"),
    SCIENCE("science", "Science"),
    SPORTS("sports", "Sports");
}