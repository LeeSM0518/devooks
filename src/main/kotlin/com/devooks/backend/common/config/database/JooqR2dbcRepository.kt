package com.devooks.backend.common.config.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Publisher
import org.jooq.Record

open class JooqR2dbcRepository(
    private val dslContext: DSLContext
) {
    suspend fun <R : Record> query(execute: DSLContext.() -> Publisher<R>): Flow<R> =
        withContext(Dispatchers.IO) {
            execute(dslContext)
        }.asFlow()
}
