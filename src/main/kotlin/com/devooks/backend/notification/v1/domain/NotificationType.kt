package com.devooks.backend.notification.v1.domain

/**
 * 알림 유형
 *
 * @property REVIEW 리뷰
 * @property REVIEW_COMMENT 리뷰 댓글
 * @property INQUIRY 문의
 * @property INQUIRY_COMMENT 문의 댓글
 * @property ANNOUNCE 공지
 * @property PURCHASE 구매
 * @property SALES 판매
 * @property WITHDRAWAL 출금
 */
enum class NotificationType {
    REVIEW, REVIEW_COMMENT, INQUIRY, INQUIRY_COMMENT, ANNOUNCE, PURCHASE, SALES, WITHDRAWAL
}
