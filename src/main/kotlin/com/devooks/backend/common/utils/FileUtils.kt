package com.devooks.backend.common.utils

import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.error.CommonError
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.pathString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

suspend inline fun saveImage(image: Image, rootPath: String): String =
    saveFileOrNull(
        extension = image.extension.toString(),
        rootPath = rootPath,
        content = image.convertDecodedImage()
    ).await()
        ?.path
        ?: throw CommonError.FAIL_SAVE_FILE.exception

fun saveFileOrNull(
    extension: String,
    rootPath: String,
    content: ByteArray,
): Deferred<File?> =
    CoroutineScope(Dispatchers.IO)
        .async {
            val fileName = UUID.randomUUID()
            val targetLocation = Path.of(rootPath, "$fileName.$extension")
            runCatching {
                Files.write(targetLocation, content).toFile()
            }.onFailure {
                val logger = logger()
                logger.error("파일 저장을 실패했습니다 [ targetLocation : ${targetLocation.pathString} ]")
                logger.error(it.stackTraceToString())
            }.getOrNull()
        }

inline fun <reified T> T.createDirectory(path: String) {
    val logger = logger()
    val directory = File(Path.of(path).toUri())
    runCatching {
        if (directory.canRead().not()) {
            directory.mkdir()
            logger.info("디렉토리 생성을 완료했습니다 [ path : ${directory.absolutePath} ]")
        } else {
            logger.info("디렉토리가 이미 존재합니다 [ path : ${directory.absolutePath} ]")
        }
    }.onFailure {
        val message = "디렉토리 생성을 실패했습니다 [ path : ${directory.absolutePath} ]"
        logger.error(message)
        logger.error(it.stackTraceToString())
        throw CommonError.FAIL_CREATE_DIRECTORY.exception
    }
}
