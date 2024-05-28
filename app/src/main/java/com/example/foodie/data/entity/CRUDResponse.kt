package com.example.foodie.data.entity

import com.google.gson.annotations.SerializedName

data class CRUDResponse(
    @SerializedName("success") var success: Int,
    @SerializedName("message") var message: String
)