package ru.emkn.kotlin.sms

import com.sksamuel.hoplite.ConfigLoader
import java.io.Reader
import java.nio.file.Paths

data class RawConfig(
    val start: Int,
    val finish: Int,
    val event: String,
    val groups: String,
    val courses: String,
)

val rawConfig = ConfigLoader()
    .loadConfigOrThrow<RawConfig>(Paths.get("src/main/resources/main.properties"))

data class Config(
    val start: Int,
    val finish: Int,
    val event: Reader,
)

val config = Config(
    rawConfig.start,
    rawConfig.finish,
    makeReader(rawConfig.event)
)