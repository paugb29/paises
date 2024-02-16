package com.example.paises

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Countries (
    val countries: List<Country>
)

data class Country (
    var isFavorite: Boolean = false,
    @SerializedName("name_en")
    val nameEn: String,
    @SerializedName("name_es")
    val nameEs: String,
    @SerializedName("continent_en")
    val continent_en: String,
    @SerializedName("continent_es")
    val continent_es: String,
    @SerializedName("capital_en")
    val capital_en: String,
    @SerializedName("capital_es")
    val capital_es: String,
    @SerializedName("dial_code")
    val dialCode: String,
    @SerializedName("code_2")
    val code2: String,
    @SerializedName("code_3")
    val code3: String,
    val tld: String,
    val km2: Double,
    val emoji: String,

    ):Serializable