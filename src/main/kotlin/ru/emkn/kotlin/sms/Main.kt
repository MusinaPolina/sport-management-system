package ru.emkn.kotlin.sms

import androidx.compose.material.Scaffold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose for Desktop",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        Scaffold(
            topBar = { menuBar() }
        ) {
            mainScreen()
        }

        if (wrongFileShow.value)
            wrongFile()
    }
}
