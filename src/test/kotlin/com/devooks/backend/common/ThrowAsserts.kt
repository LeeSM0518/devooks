package com.devooks.backend.common

import com.devooks.backend.common.exception.GeneralException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

inline fun assertThrows(exception: GeneralException, block: () -> Unit) {
    val (code, _, _) = assertThrows<GeneralException>(block)

    assertThat(code).isEqualTo(exception.code)
}