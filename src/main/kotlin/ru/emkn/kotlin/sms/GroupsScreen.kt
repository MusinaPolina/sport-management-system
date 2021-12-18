package ru.emkn.kotlin.sms

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val sorting = mutableStateOf(compareBy<Group> { it.name })

@Composable
fun groupsScreen() {
    groupsTable()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun groupsTable() {
    val column0Width = 50.dp
    val column1Width = 150.dp
    val column2Width = 200.dp
    Box(
        Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        val verticalState = rememberScrollState(0)
        val horizontalState = rememberScrollState(0)

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(verticalState)
                .horizontalScroll(horizontalState)
        ) {
            Row(Modifier.background(Color.LightGray)) {
                tableCell(text = "#", width = column0Width)
                clickableCell(text = "Название", width = column1Width) {
                    sorting.value = compareBy { it.name }
                }
                clickableCell(text = "Дистанция", width = column2Width) {
                    sorting.value = compareBy { it.course.name }
                }
            }
            for ((idx, row) in groups.sortedWith(sorting.value).withIndex()) {
                Row(Modifier.fillMaxWidth()) {
                    tableCell(text = (idx + 1).toString(), width = column0Width)
                    tableCell(text = row.name, width = column1Width)
                    tableCell(text = row.course.name, width = column2Width)
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = verticalState
            )
        )
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            adapter = rememberScrollbarAdapter(
                scrollState = horizontalState
            )
        )
    }
}