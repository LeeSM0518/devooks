package com.devooks.backend.common.config.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase

class CustomLevelColorConverter : ForegroundCompositeConverterBase<ILoggingEvent>() {

    override fun getForegroundColorCode(event: ILoggingEvent): String =
        when (event.level) {
            Level.ERROR -> RED
            Level.WARN -> YELLOW
            Level.DEBUG -> MAGENTA
            Level.TRACE -> BLUE
            Level.INFO -> GREEN
            else -> WHITE
        }.toString()

    companion object {
        private const val RED = "1;31"
        private const val GREEN = "1;32"
        private const val YELLOW = "1;33"
        private const val BLUE = "1;34"
        private const val MAGENTA = "1;35"
        private const val WHITE = "1;37"
    }
}
