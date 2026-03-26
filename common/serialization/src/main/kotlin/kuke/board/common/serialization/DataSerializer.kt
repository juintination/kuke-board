package kuke.board.common.serialization

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

object DataSerializer {

    private val log = KotlinLogging.logger {}
    private val readerCache = ConcurrentHashMap<Class<*>, ObjectReader>()
    private val writerCache = ConcurrentHashMap<Class<*>, ObjectWriter>()
    private val typeCache = ConcurrentHashMap<Class<*>, JavaType>()

    private val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    private fun getReader(
        clazz: Class<*>,
    ): ObjectReader {
        return readerCache.computeIfAbsent(clazz) {
            objectMapper.readerFor(it)
        }
    }

    private fun getWriter(
        clazz: Class<*>,
    ): ObjectWriter {
        return writerCache.computeIfAbsent(clazz) {
            objectMapper.writerFor(it)
        }
    }

    private fun getType(
        clazz: Class<*>,
    ): JavaType {
        return typeCache.computeIfAbsent(clazz) {
            objectMapper.typeFactory.constructType(it)
        }
    }

    fun <T> fromJson(
        data: String,
        clazz: Class<T>,
    ): T {
        try {
            return getReader(clazz).readValue(data)
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
        try {
            return objectMapper.convertValue(data, getType(clazz))
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[DataSerializer.convert] source=$data targetClazz=$clazz" }
            throw e
        }
    }

    fun toJson(
        obj: Any,
    ): String {
        try {
            return getWriter(obj.javaClass).writeValueAsString(obj)
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[DataSerializer.toJson] object=$obj" }
            throw e
        }
    }
}
