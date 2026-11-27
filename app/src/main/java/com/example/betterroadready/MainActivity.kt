package com.example.betterroadready

import android.os.Bundle
import android.os.SystemClock
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.navigationevent.NavigationEventInfo
import java.util.UUID
import java.lang.System
import java.time.LocalDate
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import androidx.room3.Database
import androidx.room3.RoomDatabase
import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.collectAsState
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.toDuration
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

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
            NavigationBar (containerColor = Color.Black) {
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
                       },
                       colors = NavigationBarItemDefaults.colors(
                           selectedIconColor = Color.White,
                           selectedTextColor = Color.White,
                           indicatorColor = Color.Gray
                       )
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
                HomeScreen()
            }

            composable(Screen.Drive.route) {
                DrivingScreen()
            }

            composable(Screen.Log.route) {
                DrivingLogScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {

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

        }
    }
}

@Composable
fun DrivingScreen() {

    val context = LocalContext.current
    val driveDao = remember {
        DriveDatabaseProvider.getDatabase(context).driveDao()
    }
    val scope = rememberCoroutineScope()
    
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

            var elapsedDrivingTime by remember { mutableLongStateOf(0L) }

            var isDriving by remember { mutableStateOf(false) }

            var drives by remember { mutableIntStateOf(0) }

            var startTime by remember { mutableLongStateOf(0L) }

            var endTime by remember { mutableLongStateOf(0L)}

            var isNightDrive by remember {mutableStateOf(false)}

            // Text(text = formatTime(drivingTime))

            Text(text = "Total Drives: $drives")

            Spacer(modifier = Modifier.weight(1f))


            val elapsedDrivingTimeSeconds = elapsedDrivingTime / 1000

            Text(text = formatTime(elapsedDrivingTimeSeconds))

            Button(
                onClick = {
                    isNightDrive = !isNightDrive
                }
            ) {
                Text(text = if (isNightDrive) "Night Drive: True" else "Night Drive: False")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
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
                            endTime = SystemClock.elapsedRealtime()


                            scope.launch {
                                driveDao.insert(
                                    Drive(
                                        id = randomID(),
                                        duration = elapsedDrivingTimeSeconds,
                                        startTime = startTime,
                                        endTime = endTime,
                                        isNightDrive = isNightDrive
                                    )
                                )
                            }
                            /* if isDriving is true that means the user just started a new drive
                           * so we reset to 0. but this also means that if isDriving is false
                           * we will not reset and the user can see their time. */
                        } else {
                            elapsedDrivingTime = 0
                            startTime = SystemClock.elapsedRealtime()
                        }
                    },

                    ) {
                    Text(text = if (!isDriving) "Start Drive" else "End Drive")
                }


            }
            /* This is a coroutine that is asynchronous from the rest of the program
            * if isDriving is true we will delay a second and then add one to the driving time
            * which is 1 more second*/
            LaunchedEffect(isDriving, startTime) {
                while (isDriving) {
                    elapsedDrivingTime = SystemClock.elapsedRealtime() - startTime
                    delay(1.seconds)
                }
            }
        }
    }
}

@Composable
fun DrivingLogScreen() {

    val context = LocalContext.current
    val driveDao = remember {
        DriveDatabaseProvider.getDatabase(context).driveDao()
    }

    val drives by driveDao.observeAllDrives()
        .collectAsState(initial = emptyList())

    Surface(modifier = Modifier.fillMaxSize(),
        color = Color.Black,
        contentColor = Color.White
    ) {

        LazyColumn(modifier = Modifier.fillMaxSize()
            .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp)) {
            item {
                Text(text = "Driving Log")
            }

            items(drives) { drive ->
                Text("Drive Date: ${drive.date} Drive Length: ${formatTime(drive.duration)}"
                )
            }
        }
    }
}

fun formatTime(totalSeconds: Long): String {
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

fun randomID(): String = UUID.randomUUID().toString()

@Entity(tableName = "drives")
data class Drive (
    @PrimaryKey
    val id: String = "",

    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val duration: Long,
    val startTime: Long,
    val endTime: Long,
    val isNightDrive: Boolean = false)
{
}

val drivingList = mutableListOf<Drive>()

@Dao
interface DriveDao {
    @Insert
    suspend fun insert(drive: Drive)

    @Query("SELECT * FROM drives ORDER BY startTime DESC")
    fun observeAllDrives(): Flow<List<Drive>>
}

@Database(
    entities = [Drive::class],
    version = 1
)
abstract class DriveDatabase : RoomDatabase() {
    abstract fun driveDao(): DriveDao
}

object DriveDatabaseProvider {
    @Volatile
    private var instance: DriveDatabase? = null

    fun getDatabase(context: Context): DriveDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder<DriveDatabase>(
                context.applicationContext,
                "drives.db"
            )
                .setDriver(AndroidSQLiteDriver())
                .build()
                .also { instance = it}
        }
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