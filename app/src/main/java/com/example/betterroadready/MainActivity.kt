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
        .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    )

    {
        Text(text = "Driving Log")
        var drives by remember { mutableIntStateOf(value = 0) }
        Button( onClick = {drives++}, modifier = Modifier.padding(32.dp)) {
            Text("Start New Drive")
        }

        Text(text = "Total Amount of Drives = $drives")
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