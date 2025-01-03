package com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.humanforce.humanforceandroidengineeringchallenge.R
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherUpdate
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.RequestLocationPermission
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.SimpleToast
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.getDisplayText
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.getTemperature
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.BlueDarken3
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.BlueGreyDarken1
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.BlueGreyLighten1
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.DarkTextColor
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.OrangeDarken1
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.Spacing
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.toGradientColors
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.toSmallIconResource
import com.humanforce.humanforceandroidengineeringchallenge.presentation.navigation.NavGraph
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportAction.OnPullToRefresh
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportAction.PermissionGranted
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportAction.PermissionDenied
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportAction.UpdateTemperatureUnit

/**
 * Created by kervinlevi on 24/12/24
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherReportScreen(
    state: WeatherReportState,
    onAction: (WeatherReportAction) -> Unit,
    navigateTo: (String) -> Unit
) {

    if (state.requestLocationPermissions) {
        RequestLocationPermission(onPermissionGranted = {
            onAction(PermissionGranted)
        }, onPermissionDenied = {
            onAction(PermissionDenied)
        })
    }

    Scaffold { innerPadding ->
        PullToRefreshBox(isRefreshing = state.isLoading,
            onRefresh = { onAction(OnPullToRefresh) }) {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .background(color = MaterialTheme.colorScheme.background)
            ) {

                item {
                    CurrentWeatherUpdate(state, navigateTo)
                }

                item {
                    ExtraWeatherUpdate(state)
                }

                state.weatherForecast?.updates?.let { dailyUpdates ->
                    itemsIndexed(dailyUpdates) { index, weatherUpdates ->
                        Spacer(modifier = Modifier.height(Spacing.large))
                        DailyForecastCard(index, weatherUpdates, state.activeTemperatureUnit)
                    }
                }

                state.onScreenError?.let {
                    item {
                        ScreenError(it)
                    }
                }

                item {
                    if (state.weatherForecast != null) {
                        TemperatureUnitSettings(
                            state.activeTemperatureUnit,
                            state.isLoading,
                            onAction
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(Spacing.xlarge))
                }
            }
        }
    }

    when (val event = state.oneTimeEvent?.consumeOneTimeEvent()) {
        is WeatherReportError.HttpError -> {
            SimpleToast(text = stringResource(R.string.generic_error, event.message))
        }

        WeatherReportError.LocationUnavailable -> SimpleToast(
            R.string.failed_get_location,
            length = Toast.LENGTH_LONG
        )

        WeatherReportError.NoInternet -> SimpleToast(R.string.check_internet_connection)
        null -> {}
    }
}

@Composable
fun CurrentWeatherUpdate(state: WeatherReportState, navigateTo: (String) -> Unit) {
    val current = state.weatherForecast?.current
    Box(
        modifier = Modifier.fillMaxWidth().background(
            Brush.verticalGradient(
                current?.condition.toGradientColors()
            )
        )
    ) {
        Column {
            Spacer(modifier = Modifier.height(Spacing.xlarge.times(4)))
            val temperature = LocalContext.current.getTemperature(
                current?.temperature, state.activeTemperatureUnit
            )
            Text(
                text = temperature,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Thin,
                color = DarkTextColor,
                modifier = Modifier.padding(horizontal = Spacing.large)
            )
            Text(
                text = current?.conditionDescription ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = DarkTextColor,
                modifier = Modifier.padding(horizontal = Spacing.large)
            )
            Spacer(modifier = Modifier.height(Spacing.xlarge.times(4)))

            Box {
                Column {
                    Text(
                        text = state.location.getDisplayText(max = 2),
                        maxLines = 1,
                        color = DarkTextColor,
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
                    onClick = { navigateTo(NavGraph.LOCATION) },
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
    val current = state.weatherForecast?.current
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
        modifier = modifier.padding(start = Spacing.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(drawable), contentDescription = label, tint = BlueGreyDarken1
        )
        Column(modifier = Modifier.padding(start = Spacing.normal)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Light,
            )
            Text(
                text = label, style = MaterialTheme.typography.bodyMedium, color = BlueGreyLighten1
            )
        }
    }
}

@Composable
fun DailyForecastCard(
    index: Int,
    weatherUpdates: List<WeatherUpdate>,
    activeUnit: TemperatureUnit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.normal)) {
        Box(modifier = Modifier.fillMaxWidth().background(color = OrangeDarken1)) {
            Text(
                text = weatherUpdates.firstOrNull()?.date ?: "",
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
                    Text(text = LocalContext.current.getTemperature(item.temperature, activeUnit))
                    Spacer(modifier = Modifier.height(Spacing.normal))
                }
            }
        }
    }
}

@Composable
fun ScreenError(error: WeatherReportError) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = Spacing.large),
        horizontalAlignment = Alignment.Start
    ) {

        val (title, description) = when (error) {
            is WeatherReportError.HttpError -> {
                Pair(
                    stringResource(R.string.generic_error_title),
                    stringResource(R.string.generic_error, error.message)
                )
            }

            WeatherReportError.LocationUnavailable -> {
                Pair(
                    stringResource(R.string.location_error_title),
                    stringResource(R.string.failed_get_location)
                )
            }

            WeatherReportError.NoInternet -> {
                Pair(
                    stringResource(R.string.internet_error_title),
                    stringResource(R.string.check_internet_connection)
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = error.toString(),
            tint = BlueGreyDarken1
        )
        Spacer(modifier = Modifier.height(Spacing.small))
        Text(
            text = title,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(Spacing.normal))
        Text(
            text = description,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}

@Composable
fun TemperatureUnitSettings(
    activeUnit: TemperatureUnit,
    isLoading: Boolean,
    onAction: (WeatherReportAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.7f)
            .padding(horizontal = Spacing.normal, vertical = Spacing.large)
    ) {
        Spacer(modifier = Modifier.height(Spacing.normal))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.set_temperature_unit),
                modifier = Modifier.padding(start = Spacing.normal).size(Spacing.medium)
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = stringResource(R.string.set_temperature_unit),
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.width(Spacing.normal))
        Row {
            Spacer(modifier = Modifier.width(Spacing.small))
            TemperatureUnit.entries.forEach {
                TemperatureUnitButton(
                    it,
                    activeUnit,
                    isLoading,
                    modifier = Modifier.weight(1f),
                    onAction)
                Spacer(modifier = Modifier.width(Spacing.small))
            }
        }
        Spacer(modifier = Modifier.height(Spacing.normal))
    }
}

@Composable
fun TemperatureUnitButton(
    unit: TemperatureUnit,
    activeUnit: TemperatureUnit,
    isLoading: Boolean,
    modifier: Modifier,
    onAction: (WeatherReportAction) -> Unit
) {
    TextButton(
        enabled = !isLoading && unit != activeUnit,
        onClick = { onAction(UpdateTemperatureUnit(unit)) },
        modifier = modifier
    ) {
        val textRes = when (unit) {
            TemperatureUnit.CELSIUS -> R.string.celsius_unit
            TemperatureUnit.FAHRENHEIT -> R.string.fahrenheit_unit
            TemperatureUnit.KELVIN -> R.string.kelvin_unit
        }
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.titleLarge
        )
    }
}
