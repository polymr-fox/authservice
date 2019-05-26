package com.mrfox.pupptmstr.authservice.models

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)