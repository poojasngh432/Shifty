package com.nomimon.shifty.model

data class LoginResponse(
    val id: String,
    val token: String,
    val name: String
)