package com.iurysouza.challenge.resource

data class Country(
    val capital: String,
    val name: String,
    val latlng: List<Double>,
    val country_code: String,
    val timezones: List<String>
)
