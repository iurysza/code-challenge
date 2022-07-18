package com.iurysouza.challenge.resource

import android.content.Context
import com.google.gson.Gson
import com.iurysouza.Challenge.R
import java.io.InputStream

class ResourceLoader(private val context: Context) {

    fun loadResourceFrom(resId: Int): String {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.countries)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)

    }
}
