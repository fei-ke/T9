@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.fei_ke.t9.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fei_ke.t9.R
import com.fei_ke.t9.Shortcut
import com.fei_ke.t9.widget.LifecycleAware
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Home(homeViewModel: HomeViewModel = viewModel()) {
    LifecycleAware(
        onStop = homeViewModel::scheduleClearFilter,
        onStart = homeViewModel::cancelClearFilterJob,
    )

    val uiState by homeViewModel.uiState.collectAsState()

    val containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = uiState.filter, style = MaterialTheme.typography.displaySmall)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = containerColor)
            )
        },
        containerColor = containerColor,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column {
                Box(modifier = Modifier.weight(1f)) {
                    AppList(uiState.filteredList)
                }
                Panel(onNumber = { key ->
                    homeViewModel.appendFilter(key.toString())
                }, onDelete = {
                    homeViewModel.deleteFilter()
                }, onClear = {
                    homeViewModel.clearFilter()
                })
            }
        }
    }
}

@Composable
fun AppList(appList: List<Shortcut>) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.Center,
    ) {
        items(appList) { item ->
            AppShortcut(context, item)
        }
    }
}

@Composable
private fun AppShortcut(context: Context, item: Shortcut) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val drawable by loadAppIcon(LocalContext.current, ComponentName(item.pkgName, item.className))
        val drawablePainter = rememberDrawablePainter(drawable)
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.Unspecified
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            startApp(context, item)
                        },
                        onLongClick = {
                            showMenu = true
                        }
                    )
                    .padding(8.dp)

            ) {
                Icon(
                    painter = drawablePainter,
                    contentDescription = item.label,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Unspecified,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.label,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                modifier = Modifier.defaultMinSize(minWidth = 160.dp),
                text = {
                    Text(text = stringResource(id = R.string.app_info))
                },
                onClick = {
                    startAppInfo(context, item)
                    showMenu = false
                }
            )
        }
    }
}

fun startApp(context: Context, shortcut: Shortcut) {
    if (context.packageName == shortcut.pkgName) {
        return
    }
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        setClassName(shortcut.pkgName, shortcut.className)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
    }
    context.startActivity(intent)
}

fun startAppInfo(context: Context, shortcut: Shortcut) {
    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = Uri.parse("package:${shortcut.pkgName}")
    }
    context.startActivity(intent)
}

@Composable
private fun Panel(
    items: List<Panel.Item> = Panel.items,
    onNumber: (which: Int) -> Unit = {},
    onDelete: () -> Unit = {},
    onClear: () -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.Center,
        userScrollEnabled = false
    ) {
        items(items) { item ->
            val onClick = {
                when (item) {
                    is Panel.Item.Number -> onNumber(item.key)
                    is Panel.Item.Delete -> onDelete()
                    is Panel.Item.Clear -> onClear()
                }
            }

            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color.Unspecified
            ) {
                Box(
                    modifier = Modifier
                        .combinedClickable(onClick = onClick)
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.text)
                }
            }
        }
    }
}

@Composable
private fun loadAppIcon(context: Context, componentName: ComponentName): MutableState<Drawable?> {
    val drawable = remember { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(componentName.toShortString()) {
        launch(Dispatchers.IO) {
            drawable.value = try {
                context.packageManager.getActivityIcon(componentName)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }
    }
    return drawable
}

@Preview
@Composable
fun Preview() {
    Panel()
}