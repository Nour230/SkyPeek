package com.example.skypeek.data.repository

import com.example.skypeek.data.local.FakeWeatherLocalDataSource
import com.example.skypeek.data.local.WeatherLocalDataSource
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.remote.RemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {
    private lateinit var repository: WeatherRepository
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        localDataSource = FakeWeatherLocalDataSource()
        remoteDataSource = mockk(relaxed = true)
        repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun featchWeather_returnsmockkedweatherdata()= runTest {
        //Given
        val lat = 40.7128
        val lon = -74.0060
        val apiKey = "test_api_key"
        val units = "metric"
        val lang = "en"

        val mockWeather = CurrentWeather(
            base = "stations",
            clouds = CurrentWeather.Clouds(all = 10),
            cod = 200,
            coord = CurrentWeather.Coord(lat, lon),
            dt = 1712049600,
            id = 5128581,
            main = CurrentWeather.Main(
                feels_like = 24.5,
                grnd_level = 1000,
                humidity = 60,
                pressure = 1012,
                sea_level = 1013,
                temp = 25.0,
                temp_max = 27.0,
                temp_min = 22.0
            ),
            name = "New York",
            sys = CurrentWeather.Sys(country = "US", id = 200, sunrise = 1712000000, sunset = 1712050000, type = 1),
            timezone = -14400,
            visibility = 10000,
            weather = listOf(CurrentWeather.Weather(description = "clear sky", icon = "01d", id = 800, main = "Clear")),
            wind = CurrentWeather.Wind(deg = 180, speed = 3.2)
        )
        // When RemoteDataSource is called, return mockWeather
        coEvery { remoteDataSource.getDailyWeather(lat,lon,apiKey,units, lang) } returns flowOf(mockWeather)

        val result = repository.fetchWeather(lat, lon, apiKey, units, lang).first()
        advanceUntilIdle()

        //then
        assertEquals("stations", result.base)
        assertEquals(25.0, result.main.temp, 0.0)
        assertEquals("clear sky", result.weather.first().description)
        assertEquals("New York", result.name)
        assertEquals("US", result.sys.country)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertLocation_AndReturnLocation_FromLocalDataSource() = runTest {
        // Given
        val location = LocationPOJO(lat = 30.0444,
            long = 31.2357,
            city = "Cairo",
            currentWeather = mockk(relaxed = true),
            forecast = mockk(relaxed = true))
        //when calling insertLocation
        repository.insertLocation(location)
        advanceUntilIdle()

        //then
        val result = repository.getAllLocations().first()
        assertEquals(1, result.size)
        assertEquals(location, result.first())
    }
}