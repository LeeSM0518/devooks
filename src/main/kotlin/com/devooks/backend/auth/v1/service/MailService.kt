package com.devooks.backend.auth.v1.service

import com.devooks.backend.auth.v1.dto.CheckEmailCommand
import com.devooks.backend.auth.v1.error.AuthError
import com.devooks.backend.common.utils.logger
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mailSender: JavaMailSender,
) {

    private val logger = logger()
    private val checkEmailMessage = javaClass.classLoader.getResource("static/check-email.html")?.readText()
        ?: throw NoSuchElementException("check-email.html 파일이 존재하지 않습니다.")

    fun sendCheckMessage(command: CheckEmailCommand) {
        runCatching {
            val mimeMessage = mailSender.createMimeMessage()
            val message = MimeMessageHelper(mimeMessage, false, MESSAGE_ENCODING).apply {
                setTo(command.email)
                setSubject(CHECK_MESSAGE_SUBJECT)
                setText(checkEmailMessage, true)
            }.mimeMessage
            mailSender.send(message)
        }.getOrElse { exception ->
            logger.error("이메일 전송을 실패했습니다.", exception)
            throw AuthError.FAILED_SEND_EMAIL.exception
        }
    }

    companion object {
        private const val CHECK_MESSAGE_SUBJECT = "[Devooks] 이메일 확인 안내 메일입니다."
        private const val MESSAGE_ENCODING = "UTF-8"
    }
}
