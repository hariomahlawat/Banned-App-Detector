/*
 * Copyright Ⓒ 2025 Hariom Ahlawat
 * UI screen that shows the Indian‑Army banned‑apps catalogue.
 */

package com.hariomahlawat.bannedappdetector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientEnd
import com.hariomahlawat.bannedappdetector.ui.theme.BgGradientStart
import com.hariomahlawat.bannedappdetector.ui.theme.BrandGold
import com.hariomahlawat.bannedappdetector.util.setSystemBars
import kotlinx.coroutines.launch

/* ---------- public entry ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannedAppsScreen(
    onBack: () -> Unit,
    dark: Boolean
) {
    val searchQuery = remember { mutableStateOf("") }
    val listState   = rememberLazyListState()
    val scope       = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

    setSystemBars(
        color     = if (dark) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
        darkIcons = !dark
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (dark) listOf(BgGradientStart, BgGradientEnd)
                    else listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Banned & Unwanted Apps") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                val showFab by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
                if (showFab) {
                    FloatingActionButton(
                        onClick = { scope.launch { listState.animateScrollToItem(0) } }
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Scroll to top")
                    }
                }
            },
            containerColor = Color.Transparent
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
            ) {
                SearchBar(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it }
                )

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("Banned") }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Unwanted") }
                }

                Spacer(Modifier.height(4.dp))

                // NOW this is inside ColumnScope so weight() works
                val buckets = if (selectedTab == 0) bannedAppsAZ else unwantedAppsAZ
                BannedAppsList(
                    buckets   = buckets,
                    query     = searchQuery.value,
                    listState = listState,
                    modifier  = Modifier.weight(1f)
                )
            }
        }
    }
}

/* ---------- search field ---------- */
@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Color.White.copy(alpha = .12f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Default.Search, null, tint = BrandGold)
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/* ---------- list ---------- */
@Composable
private fun BannedAppsList(
    buckets: Map<Char, List<String>>,
    query: String,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        buckets
            .filterKeys { key -> buckets[key]!!.any { it.contains(query, ignoreCase = true) } }
            .toSortedMap()
            .forEach { (letter, apps) ->

                stickyHeader {
                    Surface(
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = letter.toString(),
                            color = BrandGold,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = 20.dp, top = 6.dp, bottom = 6.dp)
                        )
                    }
                }

                items(apps.filter { it.contains(query, ignoreCase = true) }) { app ->
                    ListItem(
                        leadingContent = {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFFE53935)
                            )
                        },
                        headlineContent = { Text(app) }
                    )
                    HorizontalDivider(thickness = .5.dp, color = Color.White.copy(.12f))
                }
            }
    }
}
