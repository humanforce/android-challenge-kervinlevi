package com.humanforce.humanforceandroidengineeringchallenge.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationScreen
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportScreen
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportViewModel

/**
 * Created by kervinlevi on 24/12/24
 */
@Composable
fun RootNavigationGraph(rootNavController: NavHostController) {
    NavHost(
        navController = rootNavController,
        route = NavGraph.ROOT,
        startDestination = NavGraph.WEATHER_REPORT
    ) {
        composable(NavGraph.WEATHER_REPORT) {
            val viewModel: WeatherReportViewModel = hiltViewModel()
            WeatherReportScreen(
                state = viewModel.weatherReportState.value,
                onAction = viewModel::onAction
            )
        }

        composable(NavGraph.LOCATION) {
            LocationScreen()
        }
    }
}

object NavGraph {
    const val ROOT = "root_nav_graph"
    const val WEATHER_REPORT = "weather_report_nav_graph"
    const val LOCATION = "location_nav_graph"
}
