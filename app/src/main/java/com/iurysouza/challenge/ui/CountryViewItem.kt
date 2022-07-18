package com.iurysouza.challenge.ui

data class CountryViewItem(
    val capital: String,
    val name: String,
    val lat:Double,
    val lng:Double,
    val homeDistance:String?,
    val isHome:Boolean,
    val isSelected:Boolean = false
)
