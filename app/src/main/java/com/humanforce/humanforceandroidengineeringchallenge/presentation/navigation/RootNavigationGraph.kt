package com.humanforce.humanforceandroidengineeringchallenge.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationScreen
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationViewModel
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportAction.OnLocationUpdated
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
        startDestination = NavGraph.LOCATION
    ) {

        composable(NavGraph.WEATHER_REPORT) {
            val viewModel: WeatherReportViewModel = hiltViewModel()
            val locationState by viewModel.selectedLocation.collectAsStateWithLifecycle()
            LaunchedEffect(locationState) {
                viewModel.onAction(OnLocationUpdated)
            }

            WeatherReportScreen(
                selectedLocation = locationState,
                state = viewModel.weatherReportState.value,
                onAction = viewModel::onAction
            ) { route ->
                rootNavController.navigate(route)
            }
        }

        composable(NavGraph.LOCATION) {
            val viewModel: LocationViewModel = hiltViewModel()
            LocationScreen(
                state = viewModel.locationState.value,
                onAction = viewModel::onAction
            ) { route ->
                if (route == NavGraph.WEATHER_REPORT) {
                    rootNavController.popBackStack(route, false)
                } else {
                    rootNavController.navigate(route)
                }
            }
        }
    }
}

object NavGraph {
    const val ROOT = "root_nav_graph"
    const val WEATHER_REPORT = "weather_report_nav_graph"
    const val LOCATION = "location_nav_graph"
}
