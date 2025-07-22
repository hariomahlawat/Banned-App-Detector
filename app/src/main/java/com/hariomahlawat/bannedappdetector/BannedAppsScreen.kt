/*
 * Copyright Ⓒ 2025 Hariom Ahlawat
 * UI screen that shows the Indian‑Army banned‑apps catalogue.
 */

package com.hariomahlawat.bannedappdetector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.hariomahlawat.bannedappdetector.bannedAppsAZ

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
    setSystemBars(
        color = if (dark) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
        darkIcons = !dark
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (dark)
                        listOf(BgGradientStart, BgGradientEnd)
                    else
                        listOf(
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
                    title = { Text("Army Banned Apps") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                val showFab by remember {
                    derivedStateOf { listState.firstVisibleItemIndex > 0 }
                }
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

            Spacer(Modifier.height(4.dp))

            BannedAppsList(
                buckets   = bannedAppsAZ,
                query     = searchQuery.value,
                listState = listState,
                modifier  = Modifier.weight(1f)
            )
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
                value         = value,
                onValueChange = onValueChange,
                singleLine    = true,
                textStyle     = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                ),
                modifier      = Modifier.fillMaxWidth()
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
    LazyColumn(state = listState, modifier = modifier.fillMaxSize()) {
        buckets
            .filterKeys { key -> buckets[key]!!.any { it.contains(query, true) } }
            .toSortedMap()
            .forEach { (letter, apps) ->

                stickyHeader {
                    Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text   = letter.toString(),
                            color  = BrandGold,
                            style  = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = 20.dp, top = 6.dp, bottom = 6.dp)
                        )
                    }
                }

                items(apps.filter { it.contains(query, true) }) { app ->
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
                    Divider(thickness = .5.dp, color = Color.White.copy(.12f))
                }
            }
    }
}
