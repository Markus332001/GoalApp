package com.goal.goalapp.ui


import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.goal.goalapp.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.goal.goalapp.ui.login.StartScreen
import com.goal.goalapp.ui.login.LoginScreen

data class BottomNavigationItem(
    val title: String,
    val thisIcon: NavigationScreens,
    val selectedIcon: @Composable () -> Painter,
    val unselectedIcon: @Composable () -> Painter
)

val items = listOf(
    BottomNavigationItem(
        title = "Goals",
        thisIcon = NavigationScreens.GoalsMain,
        selectedIcon = { painterResource(id = R.drawable.goal_selected) },
        unselectedIcon = { painterResource(id = R.drawable.goal_unselected) }
    ),
    BottomNavigationItem(
        title = "Calender",
        thisIcon = NavigationScreens.CalenderMain,
        selectedIcon = { painterResource(id = R.drawable.calendar_selected) },
        unselectedIcon = { painterResource(id = R.drawable.calendar_unselected) }
    ),
    BottomNavigationItem(
        title = "Chats",
        thisIcon = NavigationScreens.ChatsMain,
        selectedIcon = { painterResource(id = R.drawable.chat_selected) },
        unselectedIcon = { painterResource(id = R.drawable.chat_unselected) }
    )
)

enum class NavigationScreens (@StringRes val title: Int){
    StartScreen(title = R.string.startScreen),
    LoginScreen(title = R.string.loginScreen),
    GoalsMain(title = R.string.goal_overview),
    CalenderMain(title = R.string.calendar),
    ChatsMain(title = R.string.chats)
}

/**
 * Composable for the bottom navigation bar with screen
 */
@Composable
fun BottomNavigation(
    selectedScreen: NavigationScreens,
    screen: @Composable (innerPadding: PaddingValues) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = selectedScreen == item.thisIcon,
                        onClick = { navController.navigate(item.thisIcon.name) },
                        icon = {
                            Icon(
                                painter = if (selectedScreen == item.thisIcon) item.selectedIcon() else item.unselectedIcon(),
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        },
        modifier = modifier
    ){ innerPadding ->
        screen(innerPadding)
    }
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
){
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = NavigationScreens.valueOf(
        backStackEntry?.destination?.route ?: NavigationScreens.GoalsMain.name
    )
    NavHost(
        navController = navController,
        startDestination = NavigationScreens.StartScreen.name,
        modifier = Modifier
    ){
        composable(route = NavigationScreens.StartScreen.name) {
            StartScreen(
                selectedScreen = NavigationScreens.StartScreen,
                toLoginScreen = {navController.navigate(NavigationScreens.LoginScreen.name)},
                toGoalOverview = {navController.navigate(NavigationScreens.GoalsMain.name)}
            )
        }
        composable(route = NavigationScreens.LoginScreen.name) {
            LoginScreen(
                selectedScreen = NavigationScreens.LoginScreen
            )
        }

        composable(route = NavigationScreens.GoalsMain.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.GoalsMain,
                screen = { Text(text = "Goals")},
                navController = navController
            )
        }
        composable(route = NavigationScreens.CalenderMain.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.CalenderMain,
                screen = { Text(text = "Calendar")},
                navController = navController
            )
        }
        composable(route = NavigationScreens.ChatsMain.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.ChatsMain,
                screen = { Text(text = "Chats")},
                navController = navController

            )
        }
    }
}


@Preview
@Composable
fun BottomNavigationPreview() {

    BottomNavigation(selectedScreen = NavigationScreens.GoalsMain, screen = {}, navController = rememberNavController())
}
