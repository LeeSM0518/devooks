package com.devooks.backend.auth.v1.domain

class Authorization(
    origin: String,
) {
    val token: AccessToken = origin.replace("Bearer ", "")
}
