package kuke.board.common.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging

object DataSerializer {

    private val log = KotlinLogging.logger {}

    private val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    fun <T> fromJson(
        data: String,
        clazz: Class<T>,
    ): T {
        try {
            return objectMapper.readValue(data, clazz)
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[DataSerializer.fromJson] data=$data clazz=$clazz" }
            throw e
        }
    }

    inline fun <reified T> fromJson(
        data: String,
    ): T {
        return fromJson(
            data = data,
            clazz = T::class.java,
        )
    }

    fun <T> convert(
        data: Any,
        clazz: Class<T>,
    ): T {
        return objectMapper.convertValue(data, clazz)
    }

    fun toJson(
        obj: Any,
    ): String {
        try {
            return objectMapper.writeValueAsString(obj)
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[DataSerializer.toJson] object=$obj" }
            throw e
        }
    }
}
