package ru.emkn.kotlin.sms

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

private val sorting = mutableStateOf(compareBy<Course> { it.name })

@Composable
fun coursesScreen() {
    coursesTable()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun coursesTable() {
    val column0Width = 50.dp
    val column1Width = 200.dp
    val column2Width = 50.dp
    val size = courses.maxOfOrNull { it.checkPoints.size } ?: 5
    val columnWidth = max(50.dp, 500.dp / size)
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
            Row(
                Modifier.background(Color.LightGray)) {
                tableCell(text = "#", width = column0Width)
                clickableCell(text = "Название", width = column1Width) {
                    sorting.value = compareBy { it.name }
                }
                clickableCell(text = "k", width = column2Width) {
                    sorting.value = compareBy { it.points }
                }
                for (i in 1..size)
                    tableCell(text = i.toString(), width = columnWidth)
            }
            for ((idx, row) in courses.sortedWith(sorting.value).withIndex()) {
                Row(Modifier.fillMaxWidth()) {
                    tableCell(text = (idx + 1).toString(), width = column0Width)
                    tableCell(text = row.name, width = column1Width)
                    tableCell(text = row.points.toString(), width = column2Width)
                    for (i in 1..size) {
                        if (i <= row.checkPoints.size)
                            tableCell(text = row.checkPoints[i - 1].toString(), width = columnWidth)
                        else
                            tableCell(text = "", width = columnWidth)
                    }
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