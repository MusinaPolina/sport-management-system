package ru.emkn.kotlin.sms

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun mainScreen() {
    val tabs = listOf(
        TabItem.Groups,
        TabItem.Courses,
        TabItem.Teams,
        TabItem.Participants,
        TabItem.Splits,
    )
    val pagerState = remember { mutableStateOf(0) }
        Scaffold(
            topBar = { tabsRow(tabs, pagerState) }
        ) {
            tabsContent(tabs, pagerState)
        }
}

@Composable
fun tabsRow(tabs: List<TabItem>, pagerState: MutableState<Int>) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.value
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                text = { Text(tab.title) },
                selected = pagerState.value == index,
                onClick = {
                    scope.launch {
                        pagerState.value = index
                    }
                },
            )
        }
    }
}

@Composable
fun tabsContent(tabs: List<TabItem>, pagerState: MutableState<Int>) {
    tabs[pagerState.value].screen()
}

@Composable
fun tableCell(
    text: String,
    width: Dp
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .width(width)
            .padding(8.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun clickableCell(
    text: String,
    width: Dp,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .border(1.dp, Color.Black)
            .width(width)
            .padding(8.dp)
            .combinedClickable {
                onClick()
            }
    ) {
        Text(text)
    }
}