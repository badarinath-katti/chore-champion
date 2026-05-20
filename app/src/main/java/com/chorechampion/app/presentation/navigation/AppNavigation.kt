package com.chorechampion.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chorechampion.app.presentation.assignments.WeeklyAssignmentsScreen
import com.chorechampion.app.presentation.auth.SignInScreen
import com.chorechampion.app.presentation.auth.SignUpScreen
import com.chorechampion.app.presentation.auth.WelcomeScreen
import com.chorechampion.app.presentation.challenge.ChallengeDetailScreen
import com.chorechampion.app.presentation.challenge.ChallengeListScreen
import com.chorechampion.app.presentation.challenge.CreateChallengeScreen
import com.chorechampion.app.presentation.challenge.JoinChallengeScreen
import com.chorechampion.app.presentation.challenge.PartnerSelectionScreen
import com.chorechampion.app.presentation.chores.ChoreDetailScreen
import com.chorechampion.app.presentation.chores.ChoreListScreen
import com.chorechampion.app.presentation.completion.CompletionScreen
import com.chorechampion.app.presentation.evaluation.EvaluationResultsScreen
import com.chorechampion.app.presentation.history.HistoryScreen
import com.chorechampion.app.presentation.home.HomeScreen
import com.chorechampion.app.presentation.profile.ProfileScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object Home : Screen("home")
    object Chores : Screen("chores")
    object ChoreDetail : Screen("chore_detail/{choreId}") {
        fun createRoute(choreId: String) = "chore_detail/$choreId"
    }
    object Completion : Screen("completion/{assignmentId}") {
        fun createRoute(assignmentId: String) = "completion/$assignmentId"
    }
    object Assignments : Screen("assignments")
    object EvaluationResults : Screen("evaluation/{weekStartDate}") {
        fun createRoute(weekStartDate: Long) = "evaluation/$weekStartDate"
        const val routeLatest = "evaluation/latest"
    }
    object History : Screen("history")
    object Profile : Screen("profile")
    object Challenges : Screen("challenges")
    object CreateChallenge : Screen("create_challenge")
    object JoinChallenge : Screen("join_challenge")
    object ChallengeDetail : Screen("challenge_detail/{challengeId}") {
        fun createRoute(challengeId: String) = "challenge_detail/$challengeId"
    }
    object PartnerSelection : Screen("partner_selection")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }
        
        composable(Screen.SignIn.route) {
            SignInScreen(navController = navController)
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Chores.route) {
            ChoreListScreen(navController = navController)
        }
        
        composable(
            route = Screen.ChoreDetail.route,
            arguments = listOf(navArgument("choreId") { type = NavType.StringType })
        ) { backStackEntry ->
            val choreId = backStackEntry.arguments?.getString("choreId") ?: return@composable
            ChoreDetailScreen(
                choreId = choreId,
                navController = navController
            )
        }
        
        composable(Screen.Assignments.route) {
            WeeklyAssignmentsScreen(navController = navController)
        }
        
        composable(
            route = Screen.Completion.route,
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: return@composable
            CompletionScreen(
                assignmentId = assignmentId,
                navController = navController
            )
        }
        
        composable(
            route = Screen.EvaluationResults.route,
            arguments = listOf(navArgument("weekStartDate") { type = NavType.LongType })
        ) { backStackEntry ->
            val weekStartDate = backStackEntry.arguments?.getLong("weekStartDate")
            EvaluationResultsScreen(
                weekStartDate = weekStartDate,
                navController = navController
            )
        }
        
        composable(Screen.EvaluationResults.routeLatest) {
            EvaluationResultsScreen(
                weekStartDate = null,
                navController = navController
            )
        }
        
        composable(Screen.Challenges.route) {
            ChallengeListScreen(navController = navController)
        }
        
        composable(Screen.CreateChallenge.route) {
            CreateChallengeScreen(navController = navController)
        }
        
        composable(Screen.JoinChallenge.route) {
            JoinChallengeScreen(navController = navController)
        }
        
        composable(
            route = Screen.ChallengeDetail.route,
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val challengeId = backStackEntry.arguments?.getString("challengeId") ?: return@composable
            ChallengeDetailScreen(
                challengeId = challengeId,
                navController = navController
            )
        }
        
        composable(Screen.PartnerSelection.route) {
            PartnerSelectionScreen(navController = navController)
        }
        
        composable(Screen.History.route) {
            HistoryScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
