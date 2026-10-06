package com.cfadmin.pro.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cfadmin.pro.ui.screens.pages.PagesDetailScreen
import com.cfadmin.pro.ui.screens.pages.PagesListScreen
import com.cfadmin.pro.ui.screens.placeholder.PlaceholderScreen

@Composable
fun CfNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Section.OVERVIEW.route
    ) {
        composable(Section.OVERVIEW.route) {
            PlaceholderScreen(Section.OVERVIEW)
        }
        composable(Section.PAGES.route) {
            PagesListScreen(
                onOpenProject = { name ->
                    navController.navigate("pages_detail/$name")
                }
            )
        }
        composable(
            route = "pages_detail/{projectName}",
            arguments = listOf(navArgument("projectName") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("projectName") ?: return@composable
            PagesDetailScreen(
                projectName = name,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Section.DNS.route) { PlaceholderScreen(Section.DNS) }
        composable(Section.TOKENS.route) { PlaceholderScreen(Section.TOKENS) }
        composable(Section.WORKERS.route) { PlaceholderScreen(Section.WORKERS) }
        composable(Section.D1.route) { PlaceholderScreen(Section.D1) }
        composable(Section.R2.route) { PlaceholderScreen(Section.R2) }
        composable(Section.KV.route) { PlaceholderScreen(Section.KV) }
        composable(Section.SPEED.route) { PlaceholderScreen(Section.SPEED) }
        composable(Section.SECURITY.route) { PlaceholderScreen(Section.SECURITY) }
    }
}