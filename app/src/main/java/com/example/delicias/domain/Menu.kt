package com.example.delicias.domain

import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int
)