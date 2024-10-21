package com.devooks.backend.common.dto

import com.devooks.backend.common.assertThrows
import com.devooks.backend.common.error.CommonError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PagingTest {

    @Test
    fun `Paging으로부터 offset과 limit을 가져올 수 있다`() {
        val list = listOf(1, 2, 3, 4)
        val paging1 = Paging(page = "1", count = "2")
        val paging2 = Paging(page = "2", count = "2")

        assertThat(list.subList(paging1.offset, paging1.limit)).containsAll(listOf(1, 2))
        assertThat(list.subList(paging2.offset, paging2.limit)).containsAll(listOf(3, 4))
    }

    @Test
    fun `페이지가 0 이하일 경우 예외가 발생한다`() {
        assertThrows(CommonError.INVALID_PAGE.exception) { Paging(page = "0", count = "2") }
    }

    @Test
    fun `개수가 0 이하이며 1000초과일 경우 예외가 발생한다`() {
        assertThrows(CommonError.INVALID_COUNT.exception) { Paging(page = "1", count = "1001") }
    }
}