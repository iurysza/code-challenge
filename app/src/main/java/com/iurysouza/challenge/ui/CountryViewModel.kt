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
    private val calculator: DistanceCalculatorImpl,
) :
    ViewModel() {

    val showCalculateResult: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val idealRoute: MutableStateFlow<String> = MutableStateFlow("")
    val countryList: MutableStateFlow<List<CountryViewItem>> = MutableStateFlow(emptyList())

    suspend fun loadNewStuff() {
        viewModelScope.launch {
            countryList.emit(loadCountryList().map {
                CountryViewItem(
                    it.name,
                    it.capital,
                    it.latlng[0],
                    it.latlng[1],
                    homeDistance = null,
                    isHome = false,
                    isSelected = false,
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

    fun onCalculateRoute() {
        viewModelScope.launch {
            val routeData = mutableListOf<Triple<String, String, Double>>()
            val route = countryList.value.filter { it.addedToTrip }
            route.forEach { startPoint ->
                val otherCities = route.minus(startPoint)
                otherCities.forEach { destination ->
                    routeData.add(
                        Triple(
                            startPoint.name,
                            destination.name,
                            calculator.distance(
                                startPoint.lat,
                                startPoint.lng,
                                destination.lat,
                                destination.lng
                            )
                        )
                    )
                }
            }
            val result = calculator.travelingSalesman(routeData)
            val message = "Ideal route is ${result?.visited()} in ${result?.distance()} km"
            idealRoute.emit(message)
        }
    }

    fun onAddToRoute(addToTrip: Boolean) {
        val countries = countryList.value

        viewModelScope.launch {
            idealRoute.emit("") //clean result
            if (!addToTrip || countries.filter { it.addedToTrip }.size < 4) {
                val newList = countries.map {
                    if (it.isSelected) it.copy(addedToTrip = addToTrip) else it
                }
                countryList.emit(newList)

                showCalculateResult.emit(newList.isNotEmpty())
            }
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
