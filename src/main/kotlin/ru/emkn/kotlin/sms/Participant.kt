package ru.emkn.kotlin.sms

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
}

val participants = mutableListOf<Participant>()