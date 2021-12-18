package ru.emkn.kotlin.sms

fun exportStarts(): Boolean {
    val file = getSaveLink() ?: return true
    try {
        Application().exportCSV(makeWriter(file))
    }
    catch (e: MyException) {
        return false
    }

    return true
}

fun exportResults(): Boolean {
    val file = getSaveLink() ?: return true
    try {
        buildResults()
        RowResults().exportCSV(makeWriter(file))
    }
    catch (e: MyException) {
        return false
    }
    return true
}

fun exportTeamResults(): Boolean {
    val file = getSaveLink() ?: return true
    try {
        RowTeamsResults().exportCSV(makeWriter(file))
    }
    catch (e: MyException) {
        return false
    }
    return true
}