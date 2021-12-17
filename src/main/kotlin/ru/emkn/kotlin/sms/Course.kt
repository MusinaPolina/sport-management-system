package ru.emkn.kotlin.sms

data class Course(val name: String, val checkPoints: List<Int>)

typealias Courses = List<Course>