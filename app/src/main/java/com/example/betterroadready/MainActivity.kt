package com.example.betterroadready

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.betterroadready.ui.theme.BetterRoadReadyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomePage()
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

        Text(text = "Total Amount of Drives = $drives", modifier = Modifier.padding(10.dp))
        Button( onClick = {drives++}, modifier = Modifier.padding(32.dp)) {
            Text("Start New Drive")
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hi, my name is $name!",
            modifier = modifier.padding(10.dp)
        )
}


@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun GreetingPreview() {
    BetterRoadReadyTheme {
        HomePage()
    }
}