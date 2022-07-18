package com.iurysouza.challenge.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
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

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = CountryViewModel(ResourceLoader(this), Gson(), DistanceCalculatorImpl())

        setContent {
            LaunchedEffect(Unit) { viewModel.loadNewStuff() }

            val countryListState = viewModel.countryList.collectAsState().value
            val showCalculateResult = viewModel.showCalculateResult.collectAsState().value
            val idealRoute = viewModel.idealRoute.collectAsState().value

            val startPosition = LatLng(51.0, 9.0) // Berlin
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(startPosition, 10f)
            }

            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    countryListState.find { it.isSelected }?.let {
                        CountryDetail(it.name,
                            it.capital,
                            it.homeDistance,
                            it.isHome,
                            it.addedToTrip,
                            onAddToTrip = { isOn -> viewModel.onAddToRoute(isOn) },
                            onSwitchHome = { viewModel.onHomeClicked() }
                        )
                    }
                    Column() {
                        if (idealRoute.isNotEmpty()) {
                            Text(text = "Ideal $idealRoute")
                        }
                        if (showCalculateResult)
                            Button(onClick = { viewModel.onCalculateRoute() }) {
                                Text(text = "Calculate Route")
                            }
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

