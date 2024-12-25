package com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.humanforce.humanforceandroidengineeringchallenge.R
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherUpdate
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.BlueDarken3
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.BlueGreyDarken1
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.BlueGreyLighten1
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.OrangeDarken1
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.Spacing
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.toGradientColors
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.toSmallIconResource
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportAction.OnPullToRefresh
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportAction.PermissionGranted
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportAction.ShowPermissionsRationale
import kotlin.math.roundToInt

/**
 * Created by kervinlevi on 24/12/24
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WeatherReportScreen(
    state: WeatherReportState, onAction: (WeatherReportAction) -> Unit
) {

    if (state.requestLocationPermissions) {
        RequestLocationPermission(onAction)
    }

    Scaffold { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(OnPullToRefresh) }) {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .background(color = MaterialTheme.colorScheme.background)
            ) {

                item {
                    CurrentWeatherUpdate(state)
                }

                item {
                    ExtraWeatherUpdate(state)
                }

                state.weatherForecast?.updates?.take(5)?.let { dailyUpdates ->
                    dailyUpdates.forEachIndexed { index, weatherUpdates ->
                        item {
                            Spacer(modifier = Modifier.height(Spacing.large))
                            DailyForecastCard(index, weatherUpdates)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherUpdate(state: WeatherReportState) {
    val current = state.weatherForecast?.updates?.firstOrNull()?.firstOrNull()
    Box(
        modifier = Modifier.fillMaxWidth().background(
            Brush.verticalGradient(
                current?.condition.toGradientColors()
            )
        )
    ) {
        Column {
            Spacer(modifier = Modifier.height(Spacing.xlarge.times(4)))
            val temperature = current?.temperature?.roundToInt()?.let { temp ->
                stringResource(R.string.celsius, temp)
            } ?: ""
            Text(
                text = temperature,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Thin,
                modifier = Modifier.padding(horizontal = Spacing.large)
            )
            Text(
                text = current?.conditionDescription ?: "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = Spacing.large)
            )
            Spacer(modifier = Modifier.height(Spacing.xlarge.times(4)))

            val location = if (state.location?.city != null && state.location.country != null) {
                "${state.location.city}, ${state.location.country}"
            } else {
                ""
            }
            Box {
                Column {
                    Text(
                        text = location,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = Spacing.large).fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(Spacing.xSmall))
                    Text(text = (state.weatherForecast?.retrievedOn)?.let {
                        stringResource(R.string.as_of_date, it)
                    } ?: "",
                        color = BlueGreyLighten1,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = Spacing.large).fillMaxWidth())
                    Spacer(modifier = Modifier.height(Spacing.normal))
                }

                FloatingActionButton(
                    modifier = Modifier.offset(x = -(Spacing.normal), y = Spacing.large)
                        .align(alignment = Alignment.CenterEnd).onGloballyPositioned {
                            it.positionOnScreen()
                        },
                    shape = FloatingActionButtonDefaults.largeShape,
                    containerColor = BlueDarken3,
                    elevation = FloatingActionButtonDefaults.elevation(Spacing.small),
                    onClick = {},
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = stringResource(R.string.update_location),
                        tint = Color.White
                    )
                }
            }

            Card(colors = CardDefaults.cardColors()
                .copy(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth().height(Spacing.large).zIndex(-1f),
                elevation = CardDefaults.cardElevation(defaultElevation = Spacing.none),
                shape = RoundedCornerShape(
                    topStart = Spacing.large, topEnd = Spacing.large
                ),
                content = {})
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtraWeatherUpdate(state: WeatherReportState) {
    val current = state.weatherForecast?.updates?.firstOrNull()?.firstOrNull()
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.large),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(Spacing.large, alignment = Alignment.Top),
        overflow = FlowRowOverflow.Visible,
        maxItemsInEachRow = 2
    ) {
        current?.humidity?.let {
            WeatherInfoTile(
                drawable = R.drawable.ic_humidity,
                label = stringResource(R.string.humidity),
                value = "$it%",
                modifier = Modifier.weight(1f)
            )
        }

        state.weatherForecast?.sunrise?.let {
            WeatherInfoTile(
                drawable = R.drawable.ic_sunrise,
                label = stringResource(R.string.sunrise),
                value = it,
                modifier = Modifier.weight(1f)
            )
        }

        current?.chanceOfRain?.let {
            WeatherInfoTile(
                drawable = R.drawable.ic_rain_chance,
                label = stringResource(R.string.chance_of_rain),
                value = "$it%",
                modifier = Modifier.weight(1f)
            )
        }

        state.weatherForecast?.sunset?.let {
            WeatherInfoTile(
                drawable = R.drawable.ic_sunset,
                label = stringResource(R.string.sunset),
                value = it,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WeatherInfoTile(drawable: Int, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding().padding(start = Spacing.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(drawable),
            contentDescription = label,
            tint = BlueGreyDarken1
        )
        Column(modifier = Modifier.padding(start = Spacing.normal)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Light,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = BlueGreyLighten1
            )
        }
    }
}

@Composable
fun DailyForecastCard(index: Int, weatherUpdates: List<WeatherUpdate>) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.normal)) {
        Box(modifier = Modifier.fillMaxWidth().background(color = OrangeDarken1)) {
            Text(
                text = if (index == 0) {
                    stringResource(R.string.today)
                } else {
                    weatherUpdates.firstOrNull()?.date ?: ""
                },
                modifier = Modifier.padding(all = Spacing.normal),
                style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 2.sp),
                color = Color.White
            )
        }
        LazyRow {
            items(weatherUpdates) { item ->
                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(item.condition.toSmallIconResource()),
                        contentDescription = item.conditionDescription,
                        tint = Color.Unspecified
                    )
                    Text(text = "${item.time}")
                    item.temperature?.roundToInt()?.let { temperature ->
                        Text(text = stringResource(R.string.celsius, temperature))
                    }
                    Spacer(modifier = Modifier.height(Spacing.normal))
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(onAction: (WeatherReportAction) -> Unit) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            if (granted.containsValue(true)) {
                onAction(PermissionGranted)
            } else {
                onAction(ShowPermissionsRationale)
            }
        }

    LaunchedEffect(key1 = permissionsState) {
        if (!permissionsState.permissions.first().status.isGranted && !permissionsState.permissions.last().status.isGranted && permissionsState.shouldShowRationale) {
            onAction(ShowPermissionsRationale)
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}
