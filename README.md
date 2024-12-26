


# Weather App
Weather App is an Android app to display weather forecasts from [OpenWeather API](https://openweathermap.org/current). The user can either select their local location or search a location by name. The coordinates of the selected location will then be used to retrieve the forecast from OpenWeather API.

## Features

- Displaying of current weather of selected location that includes temperature, humidity, chance of rain, sunrise, and sunset.
- Displaying of five-day weather forecast.
- Remembering the last selected location so that it will automatically be set when the app is opened.
- Adding locations to favorite list for easier selection.
- Caching the last retrieved weather forecast so the app can be used in offline mode.
- Selecting preferred temperature unit (Celsius/Fahrenheit/Kelvin).

## Setup

1. In the project root directory, run `cp .env.example .env`.
2. Add your OpenWeatherMap API key to the copied `.env` file.
3. Build the project in Android Studio to inject the API key defined the `.env` file to the apps `BuildConfig`.

## Android
The architecture of the code is MVVM. The libraries and tools used include:
  + Hilt (for dependency injection)
  + Jetpack Compose (for building the UI)
  + Jetpack Navigation (for the routing of Composable screens)
  + MockK (for mocking components in unit tests)
  + Moshi (to convert string to json, and vice versa)
  + Room (for storing the previously fetched weather forecast)
  + Retrofit (to retrieve remote data from OpenWeather API)

## Demo
https://github.com/user-attachments/assets/8b743f2e-6b2b-48af-bb6d-9c14c80a135d



Screenshots:
|Location permission denied|Empty search query|Empty search result|
|-|-|-|
|<img width="333" alt="Screenshot1_Location_not_granted" src="https://github.com/user-attachments/assets/97370699-6875-47df-b356-1521617ab302" />|<img width="333" alt="Screenshot2_empty_query" src="https://github.com/user-attachments/assets/2877c7d5-0abe-4194-ab40-744f3760f4f0" />|<img width="333" alt="Screenshot3_empty_search_result" src="https://github.com/user-attachments/assets/3fc9a750-b512-4b85-847f-77ace6f82573" />|

|Multiple saved locations|Displaying cached forecast|Setting temperature unit|
|-|-|-|
|<img width="333" alt="Screenshot4_multiple_favorite_locations" src="https://github.com/user-attachments/assets/e9115691-e3fd-4da0-b66e-417fe2c113c1" />|<img width="333" alt="Screenshot5_Http_error_with_cache" src="https://github.com/user-attachments/assets/0b8a69c4-80e3-4e0f-921c-835152e82900" />|<img width="333" alt="Screenshot6_setting_temperature_unit" src="https://github.com/user-attachments/assets/bb582344-8fc6-44d1-b9eb-fbcf239c5364" />|


<hr style="border:2px solid gray">


# Humanforce Android Engineering Challenge

## Overview
This challenge is designed to assess your Android engineering skills through the development of a simple weather forecast application. We're looking for innovative solutions that demonstrate strong architectural thinking, clean code principles, an eye for design,and a deep understanding of Android development best practices.

## Project Objective
Implement the requirements below to create a weather forecast application using the OpenWeatherMap API to showcase your ability to design scalable, maintainable, and high-quality Android software.

## API Endpoints
| Endpoint | Remarks |
|---|-----|
| [5 day weather forecast](https://openweathermap.org/forecast5) | Note the 3 hour time period in forecast data, this must be aggregated to get daily min/max values. |
| [GeoCoding API](https://openweathermap.org/api/geocoding-api) | Fetch coordinates for a text based search term to use in forecast/current weather API calls. |
| [Current Weather](https://openweathermap.org/current) | Fetch current conditions for the given coordinates.

# Requirements

> **Important**: Pay close attention to the requirement to aggregate 3 hour forecast windows in the 5 day forecast data to get daily min/max forecast values.

### Location-Based Weather
- Automatically fetch and display current weather for the user's current location
- Handle location services unavailability gracefully
- Provide 5-day forecast for current location

### Search and Location Management
- Implement city search functionality
  - Allow searching locations by city name
  - Display search results
  - Enable selecting a search result to view its weather
  - Show 5-day forecast for selected location

### Saved Locations
- Save favorite locations
- Display a list of saved locations
- Allow switching between saved locations

### Weather Display
- Show weather in metric units
- Use weather condition icons
  - Use provided OpenWeatherMap icons.

## Error Handling
- Gracefully manage:
  - Network connectivity issues
  - API errors
  - Location service restrictions


## Technical Constraints
- Use Kotlin & Jetpack Compose
- Implement robust error management
- Create intuitive, responsive UI

## API Configuration

### OpenWeatherMap API Setup

#### 1. Generate an OpenWeatherMap API Key
1. Visit [OpenWeatherMap](https://openweathermap.org/api)
2. Sign up for a free account
3. Generate an API key from your account dashboard

#### 2. Configure the API Key
**IMPORTANT: Never commit API keys directly to source control**

1. In the project root directory, run `cp .env.example .env`.
2. Add your OpenWeatherMap API key to the copied `.env` file.
3. Build the project in Android Studio to inject the API key defined the `.env` file to the apps `BuildConfig`.

#### 3. Consumming the API Key
An `APIKeyManager` helper object is provided for you to access the API key. If the previous two steps are followed then the API key can simply be accessed via `APIKeyManager.apiKey` anywhere in the project.

## Bonus Challenges
- Implement offline caching
- Support multiple temperature units
- Implement widget extension

## Submission Guidelines
1. Provide any additional setup instructions
2. Include comments explaining complex logic
3. Submit code that runs without additional configuration

## Time Expectation
- Estimated completion time: 4-6 hours
- Focus on quality over complete feature set

## Questions?
If you have any questions or need any clarifications, please reach out to your hiring manager or recruiter.

**Good luck!**

