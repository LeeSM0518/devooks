package com.devooks.backend.common.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PagingTest {

    @Test
    fun `Paging으로부터 offset과 limit을 가져올 수 있다`() {
        val list = listOf(1, 2, 3, 4)
        val paging1 = Paging(page = 1, count = 2)
        val paging2 = Paging(page = 2, count = 2)

        assertThat(list.subList(paging1.offset, paging1.limit)).containsAll(listOf(1, 2))
        assertThat(list.subList(paging2.offset, paging2.limit)).containsAll(listOf(3, 4))
    }
}
