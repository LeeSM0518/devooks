package com.devooks.backend.common.config.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.r2dbc.postgresql.codec.Json
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class JsonToMapConverter(
    private val objectMapper: ObjectMapper
) : Converter<Json, Map<String, Any>> {

    override fun convert(source: Json): Map<String, Any> =
        objectMapper.readValue(source.asString())
}
