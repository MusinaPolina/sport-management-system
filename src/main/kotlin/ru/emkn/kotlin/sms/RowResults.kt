package ru.emkn.kotlin.sms
import java.time.Duration
import java.time.LocalTime

data class ParticipantResult(val time: Duration, val place: Int, val delay: Duration?)

class RowResults(val participant: Participant, val result: ParticipantResult?) // null result - withdrawn