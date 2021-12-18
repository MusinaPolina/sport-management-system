package ru.emkn.kotlin.sms

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val sorting = mutableStateOf(compareBy<Team> { it.name })
private val displayingAdd = mutableStateOf(false)
private val selected = mutableStateOf<Int?>(null)

@Composable
fun teamsScreen() {
    if (displayingAdd.value)
        addTeam()
    else
        teamsTable()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun teamsTable() {
    val column0Width = 50.dp
    val column1Width = 250.dp
    val column2Width = 200.dp
    Box(
        Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        val verticalState = rememberScrollState()
        val horizontalState = rememberScrollState()

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
                clickableCell(text = "Количество участников", width = column2Width) {
                    sorting.value = compareBy { it.participants.size }
                }
            }
            for ((idx, row) in teams.sortedWith(sorting.value).withIndex()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(if (selected.value == idx) Color.LightGray else Color.Transparent)
                ) {
                    clickableCell(text = (idx + 1).toString(), width = column0Width) {
                        if (selected.value == idx)
                            selected.value = null
                        else
                            selected.value = idx
                    }
                    tableCell(text = row.name, width = column1Width)
                    tableCell(text = row.participants.size.toString(), width = column2Width)
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Button(
                    {
                        selected.value = null
                        displayingAdd.value = true
                    },
                    Modifier
                        .padding(10.dp)
                ) {
                    Text("Добавить")
                }
                if (selected.value != null) {
                    Button(
                        {
                            displayingAdd.value = true
                        },
                        Modifier
                            .padding(10.dp)
                    ) {
                        Text("Изменить")
                    }
                    Button(
                        {
                            selected.value?.let {
                                teams.removeAt(it)
                                selected.value = null
                            }
                        },
                        Modifier
                            .padding(10.dp)
                    ) {
                        Text("Удалить")
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

@Composable
private fun addTeam() {
    val name = remember { mutableStateOf(selected.value?.let { teams[it].name} ?: "") }
    Column {
        Row {
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Название") }
            )
        }
        Row {
            Button(
                {
                    val team = Team (
                        name.value,
                        mutableListOf()
                    )
                    selected.value?.let {
                        teams[it] = team
                    } ?: teams.add(team)

                    displayingAdd.value = false
                },
                Modifier
                    .padding(10.dp)
            ) {
                Text("Сохранить")
            }
            Button(
                {
                    displayingAdd.value = false
                },
                Modifier
                    .padding(10.dp)
            ) {
                Text("Отмена")
            }
        }
    }
}