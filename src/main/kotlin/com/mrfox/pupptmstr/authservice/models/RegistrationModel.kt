package com.mrfox.pupptmstr.authservice.models

import com.google.gson.annotations.SerializedName

data class RegistrationModel(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("mail")
    val mail: String
)