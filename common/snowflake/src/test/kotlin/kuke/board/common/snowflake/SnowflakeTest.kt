package kuke.board.common.snowflake

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SnowflakeTest {

    private val snowflake = Snowflake()

    @Test
    @Throws(ExecutionException::class, InterruptedException::class)
    fun nextIdTest() {
        // given
        val executorService: ExecutorService = Executors.newFixedThreadPool(10)
        val futures = mutableListOf<Future<List<Long>>>()
        val repeatCount = 1000
        val idCount = 1000

        // when
        repeat(repeatCount) {
            futures.add(executorService.submit<List<Long>> { generateIdList(snowflake, idCount) })
        }

        // then
        val result = mutableListOf<Long>()
        for (future in futures) {
            val idList = future.get()
            for (i in 1 until idList.size) {
                assertThat(idList[i]).isGreaterThan(idList[i - 1])
            }
            result.addAll(idList)
        }

        assertThat(result.distinct().count()).isEqualTo(repeatCount.toLong() * idCount)

        executorService.shutdown()
    }

    private fun generateIdList(snowflake: Snowflake, count: Int): List<Long> {
        val idList = mutableListOf<Long>()
        var remaining = count
        while (remaining-- > 0) {
            idList.add(snowflake.nextId())
        }
        return idList
    }

    @Test
    @Throws(InterruptedException::class)
    fun nextIdPerformanceTest() {
        // given
        val executorService: ExecutorService = Executors.newFixedThreadPool(10)
        val repeatCount = 1000
        val idCount = 1000
        val latch = CountDownLatch(repeatCount)

        // when
        val start = System.nanoTime()
        repeat(repeatCount) {
            executorService.submit {
                generateIdList(snowflake, idCount)
                latch.countDown()
            }
        }

        latch.await()
        val end = System.nanoTime()

        println("times = ${(end - start) / 1_000_000} ms")

        executorService.shutdown()
    }
}
