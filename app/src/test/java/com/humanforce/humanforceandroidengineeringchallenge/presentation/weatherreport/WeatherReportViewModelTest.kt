package com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport

import com.humanforce.humanforceandroidengineeringchallenge.MainDispatcherRule
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationRepository
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationUnavailableException
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit
import com.humanforce.humanforceandroidengineeringchallenge.domain.weather.WeatherRepository
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.OneTimeEvent
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportError.LocationUnavailable
import com.humanforce.humanforceandroidengineeringchallenge.testdata.favoriteLocation1
import com.humanforce.humanforceandroidengineeringchallenge.testdata.forecast1
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Created by kervinlevi on 27/12/24
 */
class WeatherReportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var weatherRepository: WeatherRepository

    @MockK
    lateinit var locationRepository: LocationRepository

    private lateinit var viewModel: WeatherReportViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { weatherRepository.getRecentWeatherForecast() } returns null
        every { weatherRepository.getTemperatureUnit() } returns TemperatureUnit.CELSIUS
        every { locationRepository.selectedLocation } returns MutableStateFlow(Location(userLocation = true))
        every { locationRepository.isLocationPermissionGranted() } returns false
        coEvery { weatherRepository.getWeatherForecast(any()) } returns Response.Success(forecast1)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given default values, verify location permission is requested`() {
        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)

        val output = viewModel.state.value
        assertTrue(output.requestLocationPermissions)
    }

    @Test
    fun `Given cached favorite location value, verify location permission is not requested`() {
        every { locationRepository.selectedLocation } returns MutableStateFlow(favoriteLocation1)

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)

        val output = viewModel.state.value
        assertFalse(output.requestLocationPermissions)
        coVerify(exactly = 1) { weatherRepository.getWeatherForecast(favoriteLocation1) }
    }

    @Test
    fun `Given non-empty location, verify weather forecast can be retrieved`() {
        every { locationRepository.isLocationPermissionGranted() } returns true
        coEvery { locationRepository.getUserLocation() } returns Response.Success(favoriteLocation1)

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)

        val output = viewModel.state.value
        coVerify(exactly = 1) { weatherRepository.getWeatherForecast(favoriteLocation1) }
        assertFalse(output.isLoading)
        assertEquals(forecast1, output.weatherForecast)
        assertEquals(favoriteLocation1, output.location)
        assertEquals(TemperatureUnit.CELSIUS, output.activeTemperatureUnit)
        assertNull(output.onScreenError)
        assertNull(output.oneTimeEvent)
    }

    @Test
    fun `Given empty location and no cached weather data, verify on screen error is shown on refresh`() {
        every { locationRepository.isLocationPermissionGranted() } returns true
        coEvery { locationRepository.getUserLocation() } returns Response.Error(
            LocationUnavailableException("")
        )

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)
        viewModel.onAction(WeatherReportAction.OnPullToRefresh)

        val output = viewModel.state.value
        assertEquals(LocationUnavailable, output.onScreenError)
        assertNull(output.oneTimeEvent)
    }

    @Test
    fun `Given empty location and has cached weather data, verify one time event error is shown when location is unavailable`() {
        every { locationRepository.isLocationPermissionGranted() } returns true
        every { weatherRepository.getRecentWeatherForecast() } returns forecast1
        coEvery { locationRepository.getUserLocation() } returns Response.Error(
            LocationUnavailableException("")
        )

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)
        viewModel.onAction(WeatherReportAction.OnPullToRefresh)

        val output = viewModel.state.value
        assertNull(output.onScreenError)
        assertEquals(OneTimeEvent(LocationUnavailable), output.oneTimeEvent)
    }

    @Test
    fun `Given onAction PermissionGranted, verify getWeatherForecast is called`() {
        every { locationRepository.isLocationPermissionGranted() } returns false
        coEvery { locationRepository.getUserLocation() } returns Response.Success(favoriteLocation1)

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)
        viewModel.onAction(WeatherReportAction.PermissionGranted)

        val output = viewModel.state.value
        coVerify(exactly = 1) { weatherRepository.getWeatherForecast(favoriteLocation1) }
        assertFalse(output.requestLocationPermissions)
    }

    @Test
    fun `Given onAction PermissionDenied, verify getWeatherForecast is not called`() {
        every { locationRepository.isLocationPermissionGranted() } returns false
        coEvery { locationRepository.getUserLocation() } returns Response.Success(favoriteLocation1)

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)
        viewModel.onAction(WeatherReportAction.PermissionDenied)

        val output = viewModel.state.value
        coVerify(exactly = 0) { weatherRepository.getWeatherForecast(favoriteLocation1) }
        assertFalse(output.requestLocationPermissions)
        assertEquals(LocationUnavailable, output.onScreenError)
        assertNull(output.oneTimeEvent)
    }

    @Test
    fun `Given onAction PermissionDenied and has cache, verify getWeatherForecast is not called`() {
        every { locationRepository.isLocationPermissionGranted() } returns false
        every { weatherRepository.getRecentWeatherForecast() } returns forecast1
        coEvery { locationRepository.getUserLocation() } returns Response.Success(favoriteLocation1)

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)
        viewModel.onAction(WeatherReportAction.PermissionDenied)

        val output = viewModel.state.value
        coVerify(exactly = 0) { weatherRepository.getWeatherForecast(favoriteLocation1) }
        assertFalse(output.requestLocationPermissions)
        assertEquals(OneTimeEvent(LocationUnavailable), output.oneTimeEvent)
        assertNull(output.onScreenError)
    }

    @Test
    fun `Given onAction OnLocationUpdated, verify getWeatherForecast is called`() {
        every { locationRepository.isLocationPermissionGranted() } returns true
        every { weatherRepository.getRecentWeatherForecast() } returns forecast1
        coEvery { locationRepository.getUserLocation() } returns Response.Success(favoriteLocation1)

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)
        viewModel.onAction(WeatherReportAction.OnLocationUpdated)

        val output = viewModel.state.value
        coVerify(exactly = 2) { weatherRepository.getWeatherForecast(favoriteLocation1) }
        assertEquals(favoriteLocation1, output.location)
        assertEquals(forecast1, output.weatherForecast)
        assertNull(output.onScreenError)
        assertNull(output.oneTimeEvent)
    }

    @Test
    fun `Given onAction OnLocationUpdated, verify unit is set and getWeatherForecast is called`() {
        every { locationRepository.isLocationPermissionGranted() } returns true
        every { weatherRepository.getRecentWeatherForecast() } returns forecast1
        coEvery { locationRepository.getUserLocation() } returns Response.Success(favoriteLocation1)

        viewModel = WeatherReportViewModel(locationRepository, weatherRepository)
        viewModel.onAction(WeatherReportAction.UpdateTemperatureUnit(TemperatureUnit.FAHRENHEIT))

        val output = viewModel.state.value
        coVerify(exactly = 2) { weatherRepository.getWeatherForecast(favoriteLocation1) }
        assertEquals(favoriteLocation1, output.location)
        assertEquals(forecast1, output.weatherForecast)
        assertNull(output.onScreenError)
        assertNull(output.oneTimeEvent)
    }
}
