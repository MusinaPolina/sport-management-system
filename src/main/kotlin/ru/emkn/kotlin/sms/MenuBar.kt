package ru.emkn.kotlin.sms

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val wrongFileShow = mutableStateOf(false)

@Composable
fun menuBar() {
    val state = rememberScrollState()
    TopAppBar (
        Modifier
            .height(40.dp)
            .horizontalScroll(state)
    ) {
        Button(
            {
                if (!importApplication())
                    wrongFileShow.value = true
            },
            modifier = Modifier
                .padding(2.dp)
                .height(30.dp)
        ) {
            Text("Import applications", fontSize = 10.sp)
        }

        Button(
            {
                if (!importStarts())
                    wrongFileShow.value = true
            },
            modifier = Modifier
                .padding(2.dp)
                .height(30.dp)
        ) {
            Text("Import start", fontSize = 10.sp)
        }
        Button(
            {
                if (!importSplits())
                    wrongFileShow.value = true
            },
            modifier = Modifier
                .padding(2.dp)
                .height(30.dp)
        ) {
            Text("Import splits", fontSize = 10.sp)
        }

        Button(
            {
                if (!importResults())
                    wrongFileShow.value = true
            },
            modifier = Modifier
                .padding(2.dp)
                .height(27.dp)
        ) {
            Text("Import results", fontSize = 10.sp)
        }

        Button(
            {
                if (!exportStarts())
                    wrongFileShow.value = true
            },
            modifier = Modifier
                .padding(2.dp)
                .height(27.dp)
        ) {
            Text("Export starts", fontSize = 10.sp)
        }

        Button(
            {
                if (!exportResults())
                    wrongFileShow.value = true
            },
            modifier = Modifier
                .padding(2.dp)
                .height(27.dp)
        ) {
            Text("Export results", fontSize = 10.sp)
        }


        Button(
            {
                if (!exportTeamResults())
                    wrongFileShow.value = true
            },
            modifier = Modifier
                .padding(2.dp)
                .height(27.dp)
        ) {
            Text("Export team results", fontSize = 10.sp)
        }
    }

    HorizontalScrollbar(
        modifier = Modifier.fillMaxWidth(),
        adapter = rememberScrollbarAdapter(
            scrollState = state
        )
    )
}

@Composable
fun wrongFile() {
    Box (
        Modifier
            .background(Color.Red)
            .padding(5.dp)
    ) {
        Column {
            Text("Wrong file", color = Color.White)
            Button(
                {
                    wrongFileShow.value = false
                }
            ) {
                Text("Ok")
            }
        }
    }
}