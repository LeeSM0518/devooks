package com.devooks.backend

import com.devooks.backend.common.config.properties.DatabaseConfig
import com.devooks.backend.common.utils.createDirectory
import com.devooks.backend.common.utils.logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
class BackendApplication(
    private val databaseConfig: DatabaseConfig,
) {
    private val logger = logger()

    init {
        logger.info("database : $databaseConfig")
    }

    companion object {
        const val STATIC_ROOT_PATH = "static"
        const val PROFILE_IMAGE_ROOT_PATH = "$STATIC_ROOT_PATH/profile-image"
        const val MAIN_IMAGE_ROOT_PATH = "$STATIC_ROOT_PATH/main-image"
        const val PDF_ROOT_PATH = "$STATIC_ROOT_PATH/pdf"
        const val PREVIEW_IMAGE_ROOT_PATH = "$STATIC_ROOT_PATH/preview"
        const val DESCRIPTION_IMAGE_ROOT_PATH = "$STATIC_ROOT_PATH/description-image"
        const val SERVICE_INQUIRY_IMAGE_ROOT_PATH = "$STATIC_ROOT_PATH/service-inquiry-image"

        fun createDirectories() {
            createDirectory(STATIC_ROOT_PATH)
            createDirectory(PROFILE_IMAGE_ROOT_PATH)
            createDirectory(MAIN_IMAGE_ROOT_PATH)
            createDirectory(PDF_ROOT_PATH)
            createDirectory(PREVIEW_IMAGE_ROOT_PATH)
            createDirectory(DESCRIPTION_IMAGE_ROOT_PATH)
            createDirectory(SERVICE_INQUIRY_IMAGE_ROOT_PATH)
        }
    }
}

fun main(args: Array<String>) {
    BackendApplication.createDirectories()
    runApplication<BackendApplication>(*args)
}
