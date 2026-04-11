# NeuroNotes AI Android App

## Setup Instructions

1. Copy the `src/main/kotlin/com/neuro notes/` folder into your Android Studio project under `app/src/main/kotlin/`.
2. Add these dependencies to your `app/build.gradle.kts` (Kotlin DSL):

```
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.ui:ui:1.5.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.8")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
}
```

3. Ensure your `build.gradle.kts` (module) has:
```
android {
    ...
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}
```

4. Run your FastAPI backend on `localhost:8000`.
5. Test in Android Emulator - it will use `10.0.2.2:8000` automatically.

## Usage

Set `DashboardScreen()` as content in your MainActivity:

```kotlin
setContent {
    NeuroNotesTheme {
        Surface(...) {
            DashboardScreen()
        }
    }
}
```

## Files Created:
- `DashboardStats.kt` - Data class for API response
- `NeuroNotesApi.kt` - Retrofit interface
- `RetrofitClient.kt` - Retrofit initialization
- `DashboardViewModel.kt` - MVVM ViewModel with StateFlow
- `DashboardScreen.kt` - Jetpack Compose UI with 2x2 stats grid

