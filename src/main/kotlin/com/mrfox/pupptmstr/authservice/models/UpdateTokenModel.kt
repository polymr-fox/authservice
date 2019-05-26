package com.mrfox.pupptmstr.authservice.models

import com.google.gson.annotations.SerializedName

data class UpdateTokenModel(
    @SerializedName("token")
    val token: String
)