package com.iurysouza.challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.iurysouza.challenge.resource.ResourceLoader
import com.iurysouza.challenge.ui.CountryViewModel
import com.iurysouza.challenge.ui.DistanceCalculatorImpl
import com.iurysouza.challenge.ui.MarkerItem

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = CountryViewModel(ResourceLoader(this), Gson(), DistanceCalculatorImpl())

        setContent {
            LaunchedEffect(Unit) { viewModel.loadNewStuff() }

            val countryListState = viewModel.countryList.collectAsState().value

            val startPosition = LatLng(51.0, 9.0)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(startPosition, 10f)
            }

            Column(Modifier.fillMaxWidth()) {
                countryListState.find { it.isSelected }?.let {
                    MarkerItem(it.name, it.capital, it.homeDistance, it.isHome) {
                        viewModel.onHomeClicked()
                    }
                }
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = {
                        viewModel.onRemoveSelection()
                    }
                ) {
                    countryListState.map { country ->
                        val position = LatLng(country.lat, country.lng)
                        Marker(
                            state = MarkerState(position = position),
                            title = country.name,
                            snippet = country.capital,
                            onClick = {
                                viewModel.onSelect(country)
                                false
                            },
                        )
                    }
                }
            }
        }
    }
}

