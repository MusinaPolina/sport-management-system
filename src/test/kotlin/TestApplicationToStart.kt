import ru.emkn.kotlin.sms.applicationsToStart
import java.io.File
import kotlin.test.*

internal class TestApplicationToStart {
    @Test
    fun checkOnePersonPerGroup() {
        val applicationFile = File.createTempFile("tmp", ".csv")
        applicationFile.writeText("""
            Team
            Группа,Фамилия,Имя,Г.р.,Разр.
            group1,LastN1,FirstN1,2000,1
            group2,LastN2,FirstN2,2001,2
            group3,LastN3,FirstN3,2002,3
        """.trimIndent())
        val application = applicationFile.bufferedReader()
        val startFile = File.createTempFile("start", ".csv")
        val start = startFile.bufferedWriter()
        applicationsToStart(listOf(application), start)
        assertEquals("""
            group1
            Номер,Фамилия,Имя,Г.р.,Разр,Команда,Время старта
            101,LastN1,FirstN1,2000,1,Team,12:00:00
            group2
            Номер,Фамилия,Имя,Г.р.,Разр,Команда,Время старта
            102,LastN2,FirstN2,2001,2,Team,12:00:00
            group3
            Номер,Фамилия,Имя,Г.р.,Разр,Команда,Время старта
            103,LastN3,FirstN3,2002,3,Team,12:00:00
        """.trimIndent().replace("\n", "\r\n") + "\r\n",
            startFile.readText())
    }

    @Test
    fun checkOneGroup() {
        val applicationFile = File.createTempFile("tmp", ".csv")
        applicationFile.writeText("""
            Team
            Группа,Фамилия,Имя,Г.р.,Разр.
            group,LastN1,FirstN1,2000,1
            group,LastN2,FirstN2,2001,2
            group,LastN3,FirstN3,2002,3
        """.trimIndent())
        val application = applicationFile.bufferedReader()
        val startFile = File.createTempFile("start", ".csv")
        val start = startFile.bufferedWriter()
        applicationsToStart(listOf(application), start)
        val text = startFile.readLines()
        assertEquals(5, text.size)
        assertEquals("group", text.first())
        assertEquals("Номер,Фамилия,Имя,Г.р.,Разр,Команда,Время старта", text[1])

        val lines = text.drop(2).map {it.split(",")}
        assertEquals("101", lines[0].first())
        assertEquals("102", lines[1].first())
        assertEquals("103", lines[2].first())

        assertEquals("12:00:00", lines[0].last())
        assertEquals("12:01:00", lines[1].last())
        assertEquals("12:02:00", lines[2].last())

        assertEquals(listOf(
            listOf("LastN1", "FirstN1", "2000", "1", "Team"),
            listOf("LastN2", "FirstN2", "2001", "2", "Team"),
            listOf("LastN3", "FirstN3", "2002", "3", "Team"),
        ),
            lines.map {it.dropLast(1).drop(1)}.sortedBy { it.first() }
        )
    }
}