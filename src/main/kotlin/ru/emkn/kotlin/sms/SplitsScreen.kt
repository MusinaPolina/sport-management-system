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

private val sorting = mutableStateOf(compareBy<Split> { it.number })

@Composable
fun splitsScreen() {
    splitsTable()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun splitsTable() {
    val column0Width = 50.dp
    val column1Width = 100.dp
    val size = splits.maxOfOrNull { it.race.checkPoints.size } ?: 2
    val columnWidth = max(100.dp, 550.dp / size - 50.dp)
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
                clickableCell(text = "Номер", width = column1Width) {
                    sorting.value = compareBy { it.number }
                }
                repeat(size) {
                    tableCell(text = "КП", width = column0Width)
                    tableCell(text = "Время", width = columnWidth)
                }
            }
            for ((idx, row) in splits.sortedWith(sorting.value).withIndex()) {
                Row(Modifier.fillMaxWidth()) {
                    tableCell(text = (idx + 1).toString(), width = column0Width)
                    tableCell(text = row.number.toString(), width = column1Width)
                    for (i in 1..size) {
                        if (i <= row.race.checkPoints.size) {
                            tableCell(text = row.race.checkPoints[i - 1].number.toString(), width = column0Width)
                            tableCell(text = row.race.checkPoints[i - 1].time.toString(), width = columnWidth)
                        }
                        else {
                            tableCell(text = "", width = column0Width)
                            tableCell(text = "", width = columnWidth)
                        }
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