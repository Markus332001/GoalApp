package com.goal.goalapp.ui


import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import com.goal.goalapp.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.goal.goalapp.ui.calender.CalendarScreen
import com.goal.goalapp.ui.group.CreateEditGroupScreen
import com.goal.goalapp.ui.group.GroupScreen
import com.goal.goalapp.ui.goal.CreateGoalScreen
import com.goal.goalapp.ui.goal.CreateGoalViewModel
import com.goal.goalapp.ui.goal.CreateRoutineScreen
import com.goal.goalapp.ui.goal.GoalDetailsScreen
import com.goal.goalapp.ui.goal.GoalOverviewScreen
import com.goal.goalapp.ui.goal.RoutineDetailsScreen
import com.goal.goalapp.ui.group.GroupChatScreen
import com.goal.goalapp.ui.group.GroupDetailsScreen
import com.goal.goalapp.ui.login.StartScreen
import com.goal.goalapp.ui.login.LoginScreen
import com.goal.goalapp.ui.login.RegisterScreen
import com.goal.goalapp.ui.settings.SettingsScreen

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
    ),
    BottomNavigationItem(
        title = "Settings",
        thisIcon = NavigationScreens.SettingsScreen,
        selectedIcon = { painterResource(id = R.drawable.settings_selected)  },
        unselectedIcon = { painterResource(id = R.drawable.settings_unselected) }
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
    CreateRoutineScreen(title = R.string.create_routine),
    GoalDetailsScreen(title = R.string.goal_details),
    RoutineDetailsScreen(title = R.string.routine_details),
    EditGoalScreen(title = R.string.edit_goal),
    EditRoutineScreen(title = R.string.edit_routine),
    CreateGroupScreen(title = R.string.create_group),
    EditGroupScreen(title = R.string.edit_group),
    GroupChatScreen(title = R.string.group_chat),
    GroupDetailsScreen(title = R.string.group_details),
    SettingsScreen(title = R.string.settings);

    // function for dynamic routes
    fun withArgs(vararg args: String): String {
        return this.name + args.joinToString("/", prefix = "/")
    }
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
    createGoalViewModel: CreateGoalViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
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
                        toGoalDetailsScreen = {goalId ->
                            navController.navigate(NavigationScreens.GoalDetailsScreen.withArgs(goalId.toString()) ) },
                        toCreateGoalScreen = { navController.navigate(NavigationScreens.CreateGoalScreen.name) },
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    )
                         },
                navController = navController
            )
        }

        composable(route = NavigationScreens.SettingsScreen.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.SettingsScreen,
                screen = { innerPadding ->
                    SettingsScreen(
                        toLoginScreen = {navController.navigate(NavigationScreens.LoginScreen.name)},
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    )
                },
                navController = navController
            )
        }

        composable(route = NavigationScreens.CreateGoalScreen.name) {
            CreateGoalScreen(
                navigateBack = { navController.navigateUp() },
                toCreateRoutineScreen = { navController.navigate(NavigationScreens.CreateRoutineScreen.name) },
                toEditRoutineScreen = {
                   navController.navigate(NavigationScreens.EditRoutineScreen.withArgs(it.toString()))
                },
                createGoalViewModel = createGoalViewModel,
                goalId = null,
                toGoalOverviewScreen = {
                    navController.navigate(NavigationScreens.GoalsMain.name)
                }
            )
        }
        composable(route = NavigationScreens.CreateRoutineScreen.name) {

            CreateRoutineScreen(
                routineId = null,
                navigateBack = { navController.navigateUp() },
                toGoalDetailsScreen = { navController.navigate(NavigationScreens.GoalDetailsScreen.withArgs(it.toString())) },
                createGoalViewModel = createGoalViewModel
            )
        }
        composable(
            route ="${NavigationScreens.EditGoalScreen.name}/{goalId}",
            arguments = listOf(navArgument("goalId") {
                type = NavType.IntType
            }) ){backStackEntry ->
            val goalId = backStackEntry.arguments?.getInt("goalId")
            CreateGoalScreen(
                goalId = goalId,
                navigateBack = { navController.navigateUp() },
                toCreateRoutineScreen = { navController.navigate(NavigationScreens.CreateRoutineScreen.name) },
                toEditRoutineScreen = {
                    navController.navigate(NavigationScreens.EditRoutineScreen.withArgs(it.toString()))
                },
               toGoalOverviewScreen = {
                   navController.navigate(NavigationScreens.GoalsMain.name)
               },
                createGoalViewModel = createGoalViewModel
            )
        }
        composable(
            route ="${NavigationScreens.EditRoutineScreen.name}/{routineId}",
            arguments = listOf(navArgument("routineId") {
                type = NavType.IntType
            })) {  backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId")
            CreateRoutineScreen(
                routineId = routineId,
                navigateBack = { navController.navigateUp() },
                toGoalDetailsScreen = { navController.navigate(NavigationScreens.GoalDetailsScreen.withArgs(it.toString())) },
                createGoalViewModel = createGoalViewModel,
            )

        }

        composable(
            route ="${NavigationScreens.GoalDetailsScreen.name}/{goalId}",
            arguments = listOf(navArgument("goalId") {
                type = NavType.IntType
            })
        ) {backStackEntry ->
            val goalId = backStackEntry.arguments?.getInt("goalId")
            GoalDetailsScreen(
                goalId = goalId,
                toRoutineDetailsScreen = { routineId ->
                    navController.navigate(NavigationScreens.RoutineDetailsScreen.withArgs(routineId.toString()))
                },
                toEditGoalScreen = { goalId ->
                    navController.navigate(NavigationScreens.EditGoalScreen.withArgs(goalId.toString()))
                },
                toGoalOverviewScreen = {
                    navController.navigate(NavigationScreens.GoalsMain.name)
                }
            )
        }

        composable(
            route ="${NavigationScreens.RoutineDetailsScreen.name}/{routineId}",
            arguments = listOf(navArgument("routineId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId")
            RoutineDetailsScreen(
                routineId = routineId,
                navigateBack = { navController.navigateUp() },
                toEditRoutineScreen = {routineId ->
                    navController.navigate(NavigationScreens.EditRoutineScreen.withArgs(routineId.toString()))
                }
            )
        }

        composable(route = NavigationScreens.CalenderMain.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.CalenderMain,
                screen = {
                    CalendarScreen(
                        modifier = Modifier.padding(bottom = it.calculateBottomPadding())
                    )
                },
                navController = navController
            )
        }
        composable(route = NavigationScreens.ChatsMain.name) {
            BottomNavigation(
                selectedScreen = NavigationScreens.ChatsMain,
                screen = {
                    GroupScreen(
                        toCreateGroupScreen = {
                            navController.navigate(NavigationScreens.CreateGroupScreen.name)
                        },
                        toGroupChatScreen = {
                            navController.navigate(NavigationScreens.GroupChatScreen.withArgs(it.toString()))
                        },
                        toGroupDetailsScreen = {
                            navController.navigate(NavigationScreens.GroupDetailsScreen.withArgs(it.toString()))
                        },
                        modifier = Modifier.padding(bottom = it.calculateBottomPadding())
                    )
                },
                navController = navController

            )
        }
        composable(route = NavigationScreens.CreateGroupScreen.name) {
            CreateEditGroupScreen(
                navigateBack = { navController.navigateUp() },
                groupId = null,
                toGroupOverviewScreen = {navController.navigate(NavigationScreens.ChatsMain.name)},
            )
        }

        composable(
            route = "${NavigationScreens.EditGroupScreen.name}/{groupId}",
            arguments = listOf(navArgument("groupId") {
                type = NavType.IntType
            })
        ) {  backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId")
            CreateEditGroupScreen(
                navigateBack = { navController.navigateUp() },
                groupId = groupId,
                toGroupOverviewScreen = {navController.navigate(NavigationScreens.ChatsMain.name)},
            )
        }

        composable(
            route = "${NavigationScreens.GroupChatScreen.name}/{groupId}",
            arguments = listOf(navArgument("groupId") {
                type = NavType.IntType
            })
        ) {  backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId")
            GroupChatScreen(
                groupId = groupId,
                navigateBack = { navController.navigateUp() },
                toGroupDetailsScreen = {groupId ->
                    navController.navigate(NavigationScreens.GroupDetailsScreen.withArgs(groupId.toString()))
                }
            )
        }

        composable(
            route = "${NavigationScreens.GroupDetailsScreen.name}/{groupId}",
            arguments = listOf(navArgument("groupId") {
                type = NavType.IntType
            })
        ) {backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId")
            GroupDetailsScreen(
                navigateBack = { navController.navigateUp() },
                groupId = groupId,
                toEditGroupScreen = {
                    navController.navigate(NavigationScreens.EditGroupScreen.withArgs(groupId.toString()))
                }
            )
        }
    }
}


@Preview
@Composable
fun BottomNavigationPreview() {

    BottomNavigation(selectedScreen = NavigationScreens.GoalsMain, screen = {}, navController = rememberNavController())
}
