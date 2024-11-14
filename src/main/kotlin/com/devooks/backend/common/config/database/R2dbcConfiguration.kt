package com.devooks.backend.common.config.database

import com.devooks.backend.common.config.properties.DatabaseConfig
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
@EnableR2dbcRepositories
class R2dbcConfiguration(
    private val databaseConfig: DatabaseConfig,
    private val customConverters: List<Converter<*, *>>,
) : AbstractR2dbcConfiguration() {

    override fun connectionFactory(): ConnectionFactory =
        ConnectionFactories.get(
            ConnectionFactoryOptions
                .builder()
                .option(ConnectionFactoryOptions.DRIVER, databaseConfig.driver)
                .option(ConnectionFactoryOptions.PROTOCOL, databaseConfig.protocol)
                .option(ConnectionFactoryOptions.HOST, databaseConfig.host)
                .option(ConnectionFactoryOptions.PORT, databaseConfig.port.toInt())
                .option(ConnectionFactoryOptions.DATABASE, databaseConfig.database)
                .option(ConnectionFactoryOptions.USER, databaseConfig.username)
                .option(ConnectionFactoryOptions.PASSWORD, databaseConfig.password)
                .build()
        )

    override fun r2dbcCustomConversions(): R2dbcCustomConversions =
        R2dbcCustomConversions(storeConversions, customConverters)

    @Bean
    fun initializer(): ConnectionFactoryInitializer =
        ConnectionFactoryInitializer()
            .apply {
                setConnectionFactory(connectionFactory())
                setDatabasePopulator(
                    ResourceDatabasePopulator(
                        ClassPathResource("schema.sql"),
                        ClassPathResource("data.sql")
                    )
                )
            }
}
