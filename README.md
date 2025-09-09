# Mobile Intern Test - Address Search App 📱

This Android application was developed as part of a mobile intern test. It provides address search functionality using the LocationIQ API and allows users to get directions to selected locations via Google Maps.

## Features

### Core Functionality

- 🔍 **Address Search**: Search for locations using LocationIQ API
- 📋 **Search Results Display**: Shows a list of matching locations
- 🗺️ **Google Maps Integration**: Tapping a result opens directions in Google Maps from current location to destination
- ✨ **Highlighted Search Keywords**: Search terms are highlighted in the results
- ⏳ **Debounced Search**: 1-second debounce on search input to prevent excessive API calls

### Technical Implementation

- 🏗️ **MVVM Architecture**: Clean separation of UI, business logic, and data
- 🧩 **Repository Pattern**: Abstracted data sources for better testability
- 🔄 **LiveData & Coroutines**: Reactive UI updates and asynchronous operations
- 🌐 **Retrofit**: Type-safe HTTP client for API communication
- 📍 **Location Services**: Integration with device location for directions

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern:

- **View Layer**: Activities and Adapters
- **ViewModel Layer**: AddressSearchViewModel
- **Model Layer**: Repository and API Services

## Setup Instructions

1. Clone the repository
2. Replace the LocationIQ API key in `LocationRepository.kt` with your own key
3. Replace the Google Maps API key in `AndroidManifest.xml` and `MainActivity.kt` with your own key
4. Build and run the application

## API Keys

The application requires two API keys:

1. **LocationIQ API Key**: For address search functionality

   - Get it from: https://locationiq.com/
   - Replace the placeholder in `LocationRepository.kt` with your actual key

2. **Google Maps API Key**: For directions functionality
   - Get it from: https://developers.google.com/maps/documentation/android-sdk/get-api-key
   - Replace the placeholder in `AndroidManifest.xml` and `MainActivity.kt` with your actual key

### API Key Security

⚠️ **Important**: The API keys in this repository are placeholders. For security reasons:

1. Never commit real API keys to public repositories
2. Replace the placeholder values with your actual keys locally
3. Consider using more secure approaches in production:
   - Environment variables
   - CI/CD secrets management
   - Server-side proxies for API calls
   - Encrypted local storage

The `.gitignore` file is configured to prevent accidental commits of files that might contain API keys.

## Video

https://github.com/user-attachments/assets/1f35b25e-43ca-4594-a455-9b685a877c59

## Requirements

- Android 7.0 (API level 24) or higher
- Google Play Services
- Internet connection
- Location permissions

## Libraries Used

- AndroidX Core and AppCompat
- Material Design Components
- ViewModel and LiveData
- Retrofit and OkHttp
- Coroutines
- Google Play Services (Maps and Location)
