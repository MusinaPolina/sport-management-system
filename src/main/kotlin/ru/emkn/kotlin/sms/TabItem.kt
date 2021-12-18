package ru.emkn.kotlin.sms

import androidx.compose.runtime.Composable

sealed class TabItem(var title: String, var screen: @Composable () -> Unit) {
    object Groups : TabItem("Группы", { groupsScreen() })
    object Courses : TabItem("Дистанции", { coursesScreen() })
    object Teams : TabItem("Команды", { teamsScreen() })
    object Participants : TabItem("Участники", { participantsScreen() })
    object Splits : TabItem("Отметки прохождения КП", { splitsScreen() })
}