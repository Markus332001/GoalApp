package com.goal.goalapp.ui


import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.goal.goalapp.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.goal.goalapp.ui.goal.CreateGoalScreen
import com.goal.goalapp.ui.goal.GoalOverviewScreen
import com.goal.goalapp.ui.login.StartScreen
import com.goal.goalapp.ui.login.LoginScreen
import com.goal.goalapp.ui.login.RegisterScreen

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
    RegisterScreen(title = R.string.registerScreen),
    StartScreen(title = R.string.startScreen),
    LoginScreen(title = R.string.loginScreen),
    GoalsMain(title = R.string.goal_overview),
    CalenderMain(title = R.string.calendar),
    ChatsMain(title = R.string.chats),
    CreateGoalScreen(title = R.string.create_goal),
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
        modifier = modifier
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
                toRegisterScreen = {navController.navigate(NavigationScreens.RegisterScreen.name)},
                toHomeScreen = {navController.navigate(NavigationScreens.GoalsMain.name)}
            )
        }
        composable(route = NavigationScreens.RegisterScreen.name) {
            RegisterScreen(
                toLoginScreen = {navController.navigate(NavigationScreens.LoginScreen.name)}
            )
        }

        composable(route = NavigationScreens.GoalsMain.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.GoalsMain,
                screen = { innerPadding ->
                    GoalOverviewScreen(
                        toGoalDetailsScreen = {goalId -> navController.navigate(NavigationScreens.GoalsMain.name + "/$goalId") },
                        toCreateGoalScreen = { navController.navigate(NavigationScreens.CreateGoalScreen.name) },
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    )
                         },
                navController = navController
            )
        }
        composable(route = NavigationScreens.CreateGoalScreen.name) {
            CreateGoalScreen(
                navigateBack = { navController.navigateUp() },
                toCreateRoutineScreen = { /*TODO*/ }
            )
        }
        composable(route = NavigationScreens.CalenderMain.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.CalenderMain,
                screen = { /*TODO*/},
                navController = navController
            )
        }
        composable(route = NavigationScreens.ChatsMain.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.ChatsMain,
                screen = { /*TODO*/},
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
