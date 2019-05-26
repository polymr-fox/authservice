package com.mrfox.pupptmstr.authservice.models

import com.google.gson.annotations.SerializedName

data class AdditionalModel(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("additionalInfo")
    val additionalInfo: String
)