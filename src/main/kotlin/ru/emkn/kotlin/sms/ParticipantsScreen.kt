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

private val sorting = mutableStateOf(compareBy<Participant> { it.number })
private val displayingAdd = mutableStateOf(false)
private val selected = mutableStateOf<Int?>(null)

@Composable
fun participantsScreen() {
    if (displayingAdd.value)
        addParticipant()
    else
        participantsTable()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun participantsTable() {
    val column0Width = 50.dp
    val column1Width = 75.dp
    val column23Width = 150.dp
    val column456Width = 100.dp
    val column7Width = 250.dp
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
                clickableCell(text = "Номер", width = column1Width) {
                    sorting.value = compareBy { it.number }
                }
                clickableCell(text = "Фамилия", width = column23Width) {
                    sorting.value = compareBy { it.lastName }
                }
                clickableCell(text = "Имя", width = column23Width) {
                    sorting.value = compareBy { it.firstName }
                }
                clickableCell(text = "Г.р.", width = column456Width) {
                    sorting.value = compareBy { it.yearOfBirth }
                }
                clickableCell(text = "Разряд", width = column456Width) {
                    sorting.value = compareBy { it.sportsCategory }
                }
                clickableCell(text = "Группа", width = column456Width) {
                    sorting.value = compareBy { it.group.name }
                }
                clickableCell(text = "Команда", width = column7Width) {
                    sorting.value = compareBy { it.team.name }
                }
            }
            for ((idx, row) in participants.sortedWith(sorting.value).withIndex()) {
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
                    tableCell(text = row.number.toString(), width = column1Width)
                    tableCell(text = row.lastName, width = column23Width)
                    tableCell(text = row.firstName, width = column23Width)
                    tableCell(text = row.yearOfBirth.toString(), width = column456Width)
                    tableCell(text = row.sportsCategory, width = column456Width)
                    tableCell(text = row.group.name, width = column456Width)
                    tableCell(text = row.team.name, width = column7Width)
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
                                participants[it].let { me ->
                                    me.team.participants.remove(me)
                                }
                                participants.removeAt(it)
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
private fun addParticipant() {
    val firstName = remember { mutableStateOf(
        selected.value?.let { participants[it].firstName} ?: ""
    ) }
    val lastName = remember { mutableStateOf(
        selected.value?.let { participants[it].lastName} ?: ""
    ) }
    val yearOfBirth = remember { mutableStateOf(
        selected.value?.let { participants[it].yearOfBirth.toString()} ?: ""
    ) }
    val sportsCategory = remember { mutableStateOf(
        selected.value?.let { participants[it].sportsCategory} ?: ""
    ) }
    val group = remember { mutableStateOf(
        selected.value?.let { participants[it].group.name} ?: ""
    ) }
    val team = remember { mutableStateOf(
        selected.value?.let { participants[it].team.name} ?: ""
    ) }
    val number : Int? = selected.value?.let { participants[it].number }
    Column {
        Row {
            OutlinedTextField(
                value = lastName.value,
                onValueChange = { lastName.value = it },
                label = { Text("Фамилия") }
            )
        }
        Row {
            OutlinedTextField(
                value = firstName.value,
                onValueChange = { firstName.value = it },
                label = { Text("Имя") }
            )
        }
        Row {
            OutlinedTextField(
                value = yearOfBirth.value,
                onValueChange = { yearOfBirth.value = it.filter { c -> c.isDigit() } },
                label = { Text("Г.р.") }
            )
        }
        Row {
            OutlinedTextField(
                value = sportsCategory.value,
                onValueChange = { sportsCategory.value = it },
                label = { Text("Разряд") }
            )
        }
        Row {
            OutlinedTextField(
                value = group.value,
                onValueChange = { group.value = it },
                label = { Text("Группа") },
                modifier = Modifier
                    .border(1.dp, if (groups.any {it.name == group.value}) Color.Unspecified else Color.Red)
            )
        }
        Row {
            OutlinedTextField(
                value = team.value,
                onValueChange = { team.value = it },
                label = { Text("Команда") }
            )
        }
        Row {
            Button(
                {
                    if (groups.any {it.name == group.value}) {
                        selected.value?.let {
                            participants[it].let { prev ->
                                prev.team.participants.remove(prev)
                            }
                        }
                        val participant = Participant(
                            number ?: lastNumber++,
                            firstName.value,
                            lastName.value,
                            yearOfBirth.value.toInt(),
                            sportsCategory.value,
                            group.value,
                            teams.find { it.name == team.value } ?: Team(team.value, mutableListOf()).also {
                                teams.add(
                                    it
                                )
                            }
                        )
                        selected.value?.let {
                            participants[it] = participant
                        } ?: participants.add(participant)

                        displayingAdd.value = false
                    }
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