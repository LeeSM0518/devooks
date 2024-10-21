package com.devooks.backend.common

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.coroutines.runBlocking
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CustomFilePart(private val file: File) : FilePart {

    override fun name(): String {
        return file.name
    }

    override fun filename(): String {
        return file.name
    }

    override fun headers(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentDisposition = org.springframework.http.ContentDisposition.builder("form-data")
            .name(name())
            .filename(filename())
            .build()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        headers.contentLength = file.length()
        return headers
    }

    override fun content(): Flux<DataBuffer> {
        return DataBufferUtils.read(file.toPath(), DefaultDataBufferFactory.sharedInstance, 4096)
    }

    override fun transferTo(dest: File): Mono<Void> {
        return Mono.fromRunnable {
            runBlocking {
                Files.copy(file.toPath(), dest.toPath())
            }
        }
    }

    override fun transferTo(dest: Path): Mono<Void> {
        return Mono.fromRunnable {
            runBlocking {
                Files.copy(file.toPath(), dest)
            }
        }
    }
}