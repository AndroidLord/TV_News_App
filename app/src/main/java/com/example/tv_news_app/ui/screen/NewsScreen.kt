@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UNUSED_EXPRESSION")

package com.example.tv_news_app.ui.screen

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.example.tv_news_app.data.model.Article
import com.example.tv_news_app.data.model.NewsResponse

@Composable
fun NewsScreen(
    newsViewModel: NewsViewModel = hiltViewModel(),
    onArticleClick: (Article) -> Unit = {},
    modifier: Modifier = Modifier
) {

    val uiState = newsViewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by newsViewModel.isRefreshing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "News",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                actions = {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        IconButton(
                            onClick = { newsViewModel.refresh() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(it)
                .focusable()
                .onKeyEvent { keyEvent ->
                    when {
                        keyEvent.key == Key.DirectionDown && keyEvent.type == KeyEventType.KeyDown -> {
                            if (keyEvent.nativeKeyEvent.isLongPress) {
                                newsViewModel.refresh()
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                }
        ) {

            CategorySelector(
                onCategorySelected = { newsViewModel.setCategory(it) },
                modifier = Modifier.fillMaxWidth()
            )

            when (val state = uiState.value) {
                is NewsUIState.Loading -> LoadingScreen(modifier)
                is NewsUIState.Error -> {
                    val message = when (state.type) {
                        NewsUIState.Error.ErrorType.EMPTY -> "No news found"
                        NewsUIState.Error.ErrorType.SERVER -> "Too many requests"
                        NewsUIState.Error.ErrorType.NETWORK -> "Network error"
                        NewsUIState.Error.ErrorType.UNKNOWN -> "Something went wrong!"
                    }
                    ErrorScreen(
                        message, modifier
                    )
                }

                is NewsUIState.Success -> {
                    NewsContent(
                        news = state.data,
                        onArticleClick = onArticleClick,
                        modifier = modifier
                    )
                }
            }
        }
    }

}

@Composable
private fun CategorySelector(
    onCategorySelected: (NewsCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(NewsCategory.HEADLINES) }

    LazyRow(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(NewsCategory.entries.toTypedArray()) { category ->
            CategoryChip(
                category = category,
                selected = selectedCategory == category,
                onClick = {
                    selectedCategory = category
                    onCategorySelected(category)
                }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: NewsCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(32.dp),
        shape = CircleShape,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        ),
        onClick = onClick
    ) {
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun NewsContent(
    news: NewsResponse,
    onArticleClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(4.dp)
            .fillMaxSize()

    ) {
        items(
            count = news.articles.size
        ) { index ->
            NewsArticleCard(
                article = news.articles[index],
                onClick = { onArticleClick(news.articles[index]) }
            )
        }
    }
}

@Composable
private fun NewsArticleCard(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Article Image
            AsyncImage(
                model = article.urlToImage,
                contentDescription = null,
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            // Article Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = article.title ?: "No title",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = article.description ?: "No description",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.author ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (article.publishedAt != null)
                        Text(
                            text = formatDate(article.publishedAt),
                            style = MaterialTheme.typography.bodySmall
                        )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = "Loading",
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Red
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val instant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.Instant.parse(dateString)
        } else {
            TODO("VERSION.SDK_INT < O")

        }
        val localDate = java.time.LocalDateTime.ofInstant(
            instant,
            java.time.ZoneId.systemDefault()
        )
        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
        localDate.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}