package com.example.skypeek.composablescreens.fav

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.models.ResponseStateFav
import com.example.skypeek.data.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavViewModelTest {
    private lateinit var viewModel: FavViewModel
    private val repo: WeatherRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = FavViewModel(repo)
    }

    @Test
    fun getFavList_whenSuccess_shouldEmitSuccessState() = runTest {
        // Given
        val mockLocations = listOf(
            LocationPOJO(51.5074, -0.1278, mockk(relaxed = true), mockk(relaxed = true), "london"),
            LocationPOJO(48.8566, 2.3522, mockk(relaxed = true), mockk(relaxed = true), "london"),

            )
        coEvery { repo.getAllLocations() } returns flow { emit(mockLocations) }

        // When
        viewModel.getFavList()
        advanceUntilIdle()

        // Then
        val result = viewModel.favList.value
        assertTrue(result is ResponseStateFav.Success)
        assertEquals(mockLocations, (result as ResponseStateFav.Success).data)
    }
}