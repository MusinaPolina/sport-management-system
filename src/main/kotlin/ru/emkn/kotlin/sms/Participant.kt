package ru.emkn.kotlin.sms

import androidx.compose.runtime.mutableStateListOf

class Participant (
    val number: Int,
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int,
    val sportsCategory: String,
    groupName: String,
    val team: Team,
) {
    val group: Group
    init {
        group = groups.find { it.name == groupName } ?: throw AbsentOfGroup(groupName)
        team.participants.add(this)
    }

    fun toList(): List<String> {
        return listOf(number.toString(), lastName, firstName, yearOfBirth.toString(), sportsCategory, team.name)
    }
}

val participants = mutableStateListOf<Participant>()