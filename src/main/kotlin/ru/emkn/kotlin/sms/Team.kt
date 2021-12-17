package ru.emkn.kotlin.sms

class Team(val name: String, val participants: MutableList<Participant>)

val teams = mutableListOf<Team>()

fun addTeam(name: String): Team {
    return teams.find { it.name == name } ?: run {
        teams.add(Team(name, mutableListOf()))
        return teams.last()
    }
}