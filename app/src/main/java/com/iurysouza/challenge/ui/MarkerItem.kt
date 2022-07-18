package com.iurysouza.challenge.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkerItem(
    title: String,
    body: String,
    homeDistance: String?,
    isHome: Boolean,
    onSwitchHome: (Boolean) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .clickable {
                Log.d("MarkerItem", "Clicked")
            }
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .background(color = Color(0xFFF5F5F5)),
        ) {
            TitleText(title)
            LabelText(body)
            Row() {
                Text("Home:")
                Switch(checked = isHome, onCheckedChange = onSwitchHome)
            }
            if (!isHome && !homeDistance.isNullOrEmpty()) {
                Text("Home distance is: $homeDistance")
            }
        }
    }
}

@Composable
private fun LabelText(label: String) {
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF000000)
                ),
            ) {
                append(label)
            }
        },
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 8.dp, top = 16.dp),
        fontSize = 16.sp,
        textAlign = TextAlign.Left,
    )
}

@Composable
private fun TitleText(value: String) {
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF000000)
                )
            ) {
                append(value)
            }
        },
        modifier = Modifier.padding(horizontal = 8.dp),
        fontSize = 19.sp,
        textAlign = TextAlign.Left,
    )
}
