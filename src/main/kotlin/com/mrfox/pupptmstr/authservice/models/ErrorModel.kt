package com.mrfox.pupptmstr.authservice.models

import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @SerializedName("status")
    val status: Int,
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("path")
    val path: String
)