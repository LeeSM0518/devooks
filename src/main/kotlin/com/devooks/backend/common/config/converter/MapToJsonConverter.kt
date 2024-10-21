package com.devooks.backend.common.config.converter

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@WritingConverter
class MapToJsonConverter(
    private val objectMapper: ObjectMapper
) : Converter<Map<String, Any>, Json> {

    override fun convert(source: Map<String, Any>): Json =
        Json.of(objectMapper.writeValueAsString(source))
}
