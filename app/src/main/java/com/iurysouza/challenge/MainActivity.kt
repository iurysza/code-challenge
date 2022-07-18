package com.iurysouza.challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.iurysouza.Challenge.R
import java.io.InputStream

class MainActivity : ComponentActivity() {

    private fun openRawResourceIntoString(resId: Int): String {
        val inputStream: InputStream = resources.openRawResource(resId)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val countryList =
            Gson().fromJson(openRawResourceIntoString(R.raw.countries), Array<Country>::class.java)
        setContent {
            val startPosition = LatLng(51.0, 9.0)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(startPosition, 10f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                countryList.map {
                    val position = LatLng(it.latlng[0], it.latlng[1])
                    Marker(
                        state = MarkerState(position = position),
                        title = it.name,
                        snippet = it.capital
                    )
                }
            }
        }
    }
}
