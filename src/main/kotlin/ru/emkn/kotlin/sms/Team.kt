package ru.emkn.kotlin.sms

import androidx.compose.runtime.mutableStateListOf

class Team(val name: String, val participants: MutableList<Participant>)

val teams = mutableStateListOf<Team>()

fun addTeam(name: String): Team {
    return teams.find { it.name == name } ?: run {
        teams.add(Team(name, mutableListOf()))
        return teams.last()
    }
}