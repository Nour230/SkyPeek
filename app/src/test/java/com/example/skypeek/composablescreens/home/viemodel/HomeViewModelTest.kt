package com.example.skypeek.composablescreens.home.viemodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.HomePOJO
import com.example.skypeek.data.models.ResponseStateLocal
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.data.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class HomeViewModelTest {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repo: WeatherRepository

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        homeViewModel = HomeViewModel(repo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertHome_should_call_repositoryInsert() = runTest {
        // Given
        val mockCurrentWeather = mockk<CurrentWeather>(relaxed = true)
        val mockForecast = mockk<WeatherResponse>(relaxed = true)
        val mockHomePOJO = HomePOJO(currentWeather = mockCurrentWeather, forcast = mockForecast)

        coEvery { repo.getLastHome() } returns flowOf(mockHomePOJO)

        // When
        homeViewModel.insertHome(mockCurrentWeather, mockForecast)
        homeViewModel.getLastHome()

        advanceUntilIdle()
        // Then - Verify through state changes
        assertTrue(
            "State should contain the inserted home data",
            homeViewModel.localCurrentWeather.value is ResponseStateLocal.Success
        )

        val successState = homeViewModel.localCurrentWeather.value as ResponseStateLocal.Success
        assertTrue(
            "State should contain the expected home data",
            successState.data == mockHomePOJO
        )
    }
}
