package com.example.betterroadready

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Surface
import org.intellij.lang.annotations.JdkConstants
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home","Home", Icons.Default.Home)
    object Log : Screen("log", "Log", Icons.Default.History)
    object Drive : Screen("drive", "Drive", Icons.Default.Add)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val items = listOf(
        Screen.Log,
        Screen.Home,
        Screen.Drive
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Read the current backstack entry to find out what screen we are on right now
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Loop through each screen item to create a tab in the dock
                items.forEach { screen ->
                   NavigationBarItem(
                       icon = {Icon(screen.icon, contentDescription = screen.title ) },
                       label = { Text(screen.title) },
                       // Is this tab currently active? Compare its route to the current screen route
                       selected = currentRoute == screen.route,
                       onClick = {
                           if (currentRoute != screen.route) {
                               navController.navigate(screen.route) {
                                   // Pop up to the start destination of the graph to
                                   // avoid building up a massive stack of screens
                                   popUpTo(navController.graph.findStartDestination().id) {
                                       saveState = true
                                   }
                                   // avoid multiple copies of the same destination when
                                   // re-selecting the same item
                                   launchSingleTop = true
                                   // restore state when re-selecting a previously selected item
                                   restoreState = true

                               }
                           }
                       }
                   )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToDrives = {
                        navController.navigate("driving")

                    },
                    onNavigateToLog = {
                        navController.navigate("log")
                    }

                )
            }

            composable(Screen.Drive.route) {
                DrivingScreen(
                    onNavigateBack = {navController.popBackStack()}
                )
            }

            composable(Screen.Log.route) {
                DrivingLogScreen(
                    onNavigateBack =  {navController.popBackStack()}
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToDrives: () -> Unit,
    onNavigateToLog: () -> Unit
) {

    Surface(modifier = Modifier.fillMaxSize(),
            color = Color.Black,
            contentColor = Color.White

    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        )

        {
            Text(text = "Better RoadReady")

            var drives by remember { mutableIntStateOf(value = 0) }

            Spacer(modifier = Modifier.weight(1f))

            var isDriving by remember { mutableStateOf(false) }

            Text(text = "Total Amount of Drives = $drives", modifier = Modifier.padding(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = onNavigateToLog
                )
                {
                    Text("Log")
                }

                Button(
                    onClick = onNavigateToDrives
                ) {
                    Text("Start Drive")
                }


            }
        }
    }
}

@Composable
fun DrivingScreen(
    onNavigateBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(),
            color = Color.Black,
            contentColor = Color.White
        ) {


        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(32.dp)
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Driving Screen")

            var drivingTime by remember { mutableIntStateOf(0) }

            var isDriving by remember { mutableStateOf(false) }

            var drives by remember { mutableIntStateOf(0) }

            Text(text = formatTime(drivingTime))

            Text(text = "Total Drives: $drives")

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {

                        /* we change isDriving's value to be whatever is the opposite of it
                        * if its true we change it to false and vice versa*/
                        isDriving = !isDriving

                        /* since when the button is clicked it changes from false to true
                        * !true is false so it only adds one to drives when click end drive
                        * because that is when isDriving is false which makes !isDriving true
                        * adding one to the drives */

                        if (!isDriving) {
                            drives++

                            /* if isDriving is true that means the user just started a new drive
                           * so we reset to 0. but this also means that if isDriving is false
                           * we will not reset and the user can see their time. */
                        } else {
                            drivingTime = 0
                        }
                    },

                    ) {
                    Text(text = if (!isDriving) "Start Drive" else "End Drive")
                }

                Button(
                    onClick = onNavigateBack
                ) {
                    Text("Back")
                }
            }
            /* This is a coroutine that is asynchronous from the rest of the program
            * if isDriving is true we will delay a second and then add one to the driving time
            * which is 1 more second*/
            LaunchedEffect(isDriving) {
                while (isDriving) {
                    delay(1.seconds)
                    drivingTime++
                }
            }
        }
    }
}

@Composable
fun DrivingLogScreen(
    onNavigateBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(),
        color = Color.Black,
        contentColor = Color.White

    ) {
        Column(modifier = Modifier.fillMaxSize()
            .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp)

            ) {
            Text("Driving Log")


            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onNavigateBack) {
                Text("Back")
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
        AppNavigation()
    }
}