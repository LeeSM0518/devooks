package com.devooks.backend.auth.v1.config

import java.util.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfiguration(
    private val mailProperties: MailProperties,
) {

    @Bean
    fun javaMailSender(): JavaMailSender =
        JavaMailSenderImpl().apply {
            host = mailProperties.host
            port = mailProperties.port
            username = mailProperties.username
            password = mailProperties.password
            protocol = mailProperties.protocol
            javaMailProperties = Properties().apply {
                put("mail.smtp.auth", mailProperties.auth)
                put("mail.smtp.timout", mailProperties.timeout)
                put("mail.smtp.starttls.enable", mailProperties.tls)
                put("mail.transport.protocol", mailProperties.protocol)
                put("mail.debug", mailProperties.debug)
            }
        }

}
