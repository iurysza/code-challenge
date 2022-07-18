package com.iurysouza.challenge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.iurysouza.Challenge.R
import com.iurysouza.challenge.resource.Country
import com.iurysouza.challenge.resource.ResourceLoader
import java.text.DecimalFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CountryViewModel(
    private val resourceLoader: ResourceLoader,
    private val gson: Gson,
    val calculator: DistanceCalculatorImpl,
) :
    ViewModel() {

    val home: MutableStateFlow<CountryViewItem?> = MutableStateFlow(null)
    val countryList: MutableStateFlow<List<CountryViewItem>> = MutableStateFlow(emptyList())

    suspend fun loadNewStuff() {
        viewModelScope.launch {
            countryList.emit(loadCountryList().map {
                CountryViewItem(it.name,
                    it.capital,
                    it.latlng[0],
                    it.latlng[1],
                    isSelected = false,
                    isHome = false,
                    homeDistance = null
                )
            })
        }
    }

    private fun loadCountryList(): Array<Country> {
        val countryDataset = resourceLoader.loadResourceFrom(R.raw.countries)
        return gson.fromJson(countryDataset, Array<Country>::class.java)
    }

    fun onSelect(country: CountryViewItem) {
        val distFromHome = calculateAndFormatDistanceFromHome(country)
        viewModelScope.launch {
            countryList.emit(countryList.value.map {
                it.copy(
                    isSelected = it.name == country.name,
                    homeDistance = distFromHome
                )
            })
        }
    }

    private fun calculateAndFormatDistanceFromHome(selectedCountry: CountryViewItem): String? {
        return countryList.value.find { it.isHome }?.let { home ->
            val distanceFromHome = calculator.distance(
                home.lat,
                home.lng,
                selectedCountry.lat,
                selectedCountry.lng
            )
            DecimalFormat("#.##").format(distanceFromHome) + " km"
        }
    }

    fun onHomeClicked() {
        viewModelScope.launch {
            countryList.emit(countryList.value.map {
                it.copy(isHome = it.isSelected)
            })
        }
    }

    fun onRemoveSelection() {
        viewModelScope.launch {
            countryList.emit(countryList.value.map {
                it.copy(isSelected = false)
            })
        }
    }
}
