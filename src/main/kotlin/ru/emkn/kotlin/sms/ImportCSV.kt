package ru.emkn.kotlin.sms

fun importApplication(): Boolean {
    val files = getOpenLinkMultiple()
    try {
        if (!Application().checkApplications(files.map { makeReader(it) }))
            return false
        else
            Application().readApplications(files.map { makeReader(it) })
    } catch (e: MyException) {
        return false
    }
    return true
}

fun importStarts(): Boolean {
    val file = getOpenLink() ?: return true
    try {
        if (!checkStartTime(makeReader(file)))
            return false
        else
            startTimeParse(makeReader(file))
    } catch (e: MyException) {
        return false
    }
    return true
}

fun importSplits(): Boolean {
    val file = getOpenLink() ?: return true
    try {
        if (!checkSplits(makeReader(file)))
            return false
        else
            splitsParse(makeReader(file))
    } catch (e: MyException) {
        return false
    }
    return true
}

fun importResults(): Boolean {
    val file = getOpenLink() ?: return true
    try {
        if (!checkResultsProtocol(makeReader(file)))
            return false
        else
            parseResultsProtocol(makeReader(file))
    }
    catch (e: MyException) {
        return false
    }
    return true
}