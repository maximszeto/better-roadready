package com.example.betterroadready

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.betterroadready.ui.theme.BetterRoadReadyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrivingScreen()
        }
    }
}


@Composable
fun HomePage() {
    Column(modifier = Modifier.fillMaxWidth()
        .padding(32.dp)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    )

    {
        Text(text = "Driving Log")

        Column(modifier = Modifier
            .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 100.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text("Drive 1 ")
                Text("Time 20mins")
            }

            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text("Drive 2")
                Text("Time 35mins")
            }
        }

        var drives by remember { mutableIntStateOf(value = 0) }

        Spacer(modifier = Modifier.weight(1f))

        var isDriving by remember { mutableStateOf(false) }

        Text(text = "Total Amount of Drives = $drives", modifier = Modifier.padding(10.dp))
        Button(
            onClick = {
                isDriving = !isDriving

                if (!isDriving) {
                    drives++
                }

            },

            modifier = Modifier.padding(32.dp)

        ) {
            Text(text =  if (isDriving) "End Drive" else "Start Drive")
        }

    }
}

@Composable
fun DrivingScreen() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Driving Screen")

        var drivingTime by remember { mutableIntStateOf(0) }

        var isDriving by remember { mutableStateOf(false) }

        var drives by remember { mutableIntStateOf(0)}

        Text(text = formatTime(drivingTime))

        Text(text = "Total Drives: $drives")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                isDriving = !isDriving

                if(!isDriving) {
                    drives++
                }
            },

        ) {
            Text(text = if(!isDriving) "Start Drive" else "End Drive")
        }


        LaunchedEffect(isDriving) {
            while (isDriving) {
                delay(1.seconds)
                drivingTime++
            }
        }
    }
}

fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(
        Locale.US, "%02d:%02d:%02d",
        hours,
        minutes,
        seconds
    )
}


@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun GreetingPreview() {
    BetterRoadReadyTheme {
       DrivingScreen()
    }
}