package com.humanforce.humanforceandroidengineeringchallenge.presentation.location

import com.humanforce.humanforceandroidengineeringchallenge.MainDispatcherRule
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationRepository
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.OneTimeEvent
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationEvent.SaveFavoriteFailed
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationEvent.SaveFavoriteSuccessful
import com.humanforce.humanforceandroidengineeringchallenge.testdata.favoriteLocation1
import com.humanforce.humanforceandroidengineeringchallenge.testdata.searchResponseEmpty
import com.humanforce.humanforceandroidengineeringchallenge.testdata.searchResponseError
import com.humanforce.humanforceandroidengineeringchallenge.testdata.searchResponseHttpError
import com.humanforce.humanforceandroidengineeringchallenge.testdata.searchResponseSuccess
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by kervinlevi on 27/12/24
 */
class LocationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var locationRepository: LocationRepository

    private lateinit var viewModel: LocationViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { locationRepository.selectedLocation } returns MutableStateFlow(Location(userLocation = true))
        every { locationRepository.favoriteLocations } returns MutableStateFlow(emptyList())
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given user typed a query, verify the state is saved`() {
        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.UpdateQuery("Test"))

        val output = viewModel.locationState.value
        assertEquals("Test", output.query)
    }

    @Test
    fun `Given user typed a query and search, verify the query is used`() {
        coEvery { locationRepository.searchLocations(any()) } returns searchResponseSuccess

        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.UpdateQuery("Test"))
        viewModel.onAction(LocationAction.SearchLocation)

        val output = viewModel.locationState.value
        coVerify(exactly = 1) { locationRepository.searchLocations("Test") }
        assertEquals("Test", output.query)
        assertEquals(searchResponseSuccess.data, output.searchResults)
        assertNull(output.searchError)
    }

    @Test
    fun `Given empty query and search, verify the error`() {
        coEvery { locationRepository.searchLocations(any()) } returns searchResponseSuccess

        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.UpdateQuery(""))
        viewModel.onAction(LocationAction.SearchLocation)

        val output = viewModel.locationState.value
        coVerify(exactly = 0) { locationRepository.searchLocations(any()) }
        assertEquals("", output.query)
        assertEquals(LocationError.EmptyQuery, output.searchError)
    }

    @Test
    fun `Given non-empty query but empty search result, verify the error`() {
        coEvery { locationRepository.searchLocations(any()) } returns searchResponseEmpty

        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.UpdateQuery("Test"))
        viewModel.onAction(LocationAction.SearchLocation)

        val output = viewModel.locationState.value
        coVerify(exactly = 1) { locationRepository.searchLocations("Test") }
        assertEquals("Test", output.query)
        assertEquals(LocationError.EmptyResult, output.searchError)
    }

    @Test
    fun `Given search result contains error, verify the state`() {
        coEvery { locationRepository.searchLocations(any()) } returns searchResponseError

        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.UpdateQuery("Test"))
        viewModel.onAction(LocationAction.SearchLocation)

        val output = viewModel.locationState.value
        coVerify(exactly = 1) { locationRepository.searchLocations("Test") }
        assertEquals("Test", output.query)
        assertEquals(LocationError.NoInternet, output.searchError)
    }

    @Test
    fun `Given search result contains http error, verify the state`() {
        coEvery { locationRepository.searchLocations(any()) } returns searchResponseHttpError

        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.UpdateQuery("Test"))
        viewModel.onAction(LocationAction.SearchLocation)

        val output = viewModel.locationState.value
        coVerify(exactly = 1) { locationRepository.searchLocations("Test") }
        assertEquals("Test", output.query)
        assertTrue(output.searchError is LocationError.HttpError)
    }

    @Test
    fun `Given user selected a location, verify the location set is correct`() {
        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.UpdateLocation(favoriteLocation1))

        val outputSlot = slot<Location>()
        verify(exactly = 1) { locationRepository.updateLocation(capture(outputSlot)) }
        assertEquals(favoriteLocation1, outputSlot.captured)
    }

    @Test
    fun `Given onAction UseUserLocation, verify the location set`() {
        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.UseUserLocation)

        val outputSlot = slot<Location>()
        verify(exactly = 1) { locationRepository.updateLocation(capture(outputSlot)) }
        assertTrue(outputSlot.captured.userLocation)
    }

    @Test
    fun `Given successful add favorite location, verify one time event`() {
        coEvery { locationRepository.addFavoriteLocation(any()) } returns Response.Success(1L)

        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.SaveLocation(favoriteLocation1))

        val output = viewModel.locationState.value
        assertEquals(OneTimeEvent(SaveFavoriteSuccessful), output.oneTimeEvent)
    }

    @Test
    fun `Given unsuccessful add favorite location, verify one time event`() {
        coEvery { locationRepository.addFavoriteLocation(any()) } returns Response.Error(Exception(""))

        viewModel = LocationViewModel(locationRepository)
        viewModel.onAction(LocationAction.SaveLocation(favoriteLocation1))

        val output = viewModel.locationState.value
        assertEquals(OneTimeEvent(SaveFavoriteFailed), output.oneTimeEvent)
    }
}
