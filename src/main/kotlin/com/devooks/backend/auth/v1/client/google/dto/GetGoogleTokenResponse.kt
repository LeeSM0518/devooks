package com.devooks.backend.auth.v1.client.google.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GetGoogleTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String?
)
