package com.iurysouza.challenge

data class Country(
    val capital: String,
    val country_code: String,
    val latlng: List<Double>,
    val name: String,
    val timezones: List<String>
)
