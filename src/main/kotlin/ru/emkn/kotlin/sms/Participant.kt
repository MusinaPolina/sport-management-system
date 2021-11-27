package ru.emkn.kotlin.sms

data class Participant (
    val firstName: String,
    val lastName: String,
    val yearOfBirth: Int,
    val sportsCategory: String,
    val group: String,
    val team: String,
)