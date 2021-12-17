package ru.emkn.kotlin.sms

data class Group(val name: String, val course: Course, val leader: Int)

typealias Groups = List<Group>
