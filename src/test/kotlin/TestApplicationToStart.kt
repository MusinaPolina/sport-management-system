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
}