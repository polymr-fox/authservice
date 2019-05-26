package com.mrfox.pupptmstr.authservice.models

import com.google.gson.annotations.SerializedName
import javax.print.attribute.standard.RequestingUserName

data class UserModel(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("username")
    val userName: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("mail")
    val mail: String,
    @SerializedName("links")
    val links: List<String>,
    @SerializedName("role")
    val role: String,
    @SerializedName("canTags")
    val canTags: List<String>,
    @SerializedName("loveTags")
    val loveTags: List<String>,
    @SerializedName("token")
    val token: String
)