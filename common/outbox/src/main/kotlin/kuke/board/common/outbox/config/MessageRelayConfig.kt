package kuke.board.common.outbox.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@EnableAsync
@Configuration
@ComponentScan("kuke.board.common.outbox")
@EnableScheduling
class MessageRelayConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
) {

    @Bean
    fun messageRelayKafkaTemplate(): KafkaTemplate<String, String> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.ACKS_CONFIG to "all"
        )
        return KafkaTemplate(DefaultKafkaProducerFactory(configProps))
    }

    @Bean
    fun messageRelayPublishEventExecutor(): Executor =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 20
            maxPoolSize = 50
            setQueueCapacity(100)
            setThreadNamePrefix("mr-pub-event-")
            initialize()
        }

    @Bean
    fun messageRelayPublishPendingEventExecutor(): Executor =
        Executors.newSingleThreadScheduledExecutor()
}
