package com.android.hwsystemmanager.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peihua.touchmonitor.ui.icons.AppIcons

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    hostState: SnackbarHostState = remember { snackbarHostState },
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions
            )
        }, snackbarHost = { SnackbarHost(hostState) }) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            content()
        }
    }
}

val snackbarHostState = SnackbarHostState()

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String,
    navigateUp: () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {
        NavigationIcon(navigateUp = navigateUp)
    },
    actions: @Composable RowScope.() -> Unit = {},
    hostState: SnackbarHostState = remember { snackbarHostState },
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions
            )
        }, snackbarHost = { SnackbarHost(hostState) }) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier, title: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    val typography = MaterialTheme.typography
    val colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors()
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                ScaleText(
                    style = typography.titleMedium,
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth(Alignment.CenterHorizontally) // 水平居中
                        .align(Alignment.Center),
                    fontSize = 24.sp,
                )
            }
        },
        navigationIcon = navigationIcon,
        actions = actions,
        modifier = modifier,
        colors = colors
    )
}

/**
 * 导航图标
 */
@Composable
fun NavigationIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = AppIcons.IosArrowBack,
    navigateUp: () -> Unit = {},
) {
    NavigationIcon2(
        modifier
            .size(48.dp)
            .clip(shape = RoundedCornerShape(8.dp)),
        imageVector, navigateUp = navigateUp
    )
}

/**
 * 导航图标
 */
@Composable
fun NavigationIcon2(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = AppIcons.IosArrowBack,
    tintColor: Color = Color.Black,
    navigateUp: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { navigateUp() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(4.dp)
                .aspectRatio(1f)
                .fillMaxSize(),
            imageVector = imageVector,
            tint = tintColor,
            contentDescription = ""
        )
    }
}