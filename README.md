# Planify

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue)
![Android](https://img.shields.io/badge/Platform-Android-green)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-purple)

![Planify banner](assets/banner.png)

## Overview

Planify is a mobile application designed to help students organize their
time and study more effectively.

The goal is to provide a complete toolkit for academic productivity in a
single place. Instead of using multiple apps, students can manage their
academic life through one unified experience.

Core ideas of the product include:

-   Personal planner for tasks and schedules
-   Study tools inspired by spaced repetition
-   Focus techniques such as Pomodoro
-   Centralized academic workflow


## Running Locally

### Requirements

-   Java 17+
-   Android SDK (API 36)
-   USB debugging enabled on your phone

### First time setup (Linux)

If you don't have Android Studio, the setup script installs the SDK,
accepts licenses and configures environment variables automatically:

``` bash
./.scripts/setup-android-linux.sh
```

Then add your environment variables (see section below) and connect your phone.

### Daily workflow

Build and install the debug APK on your connected device:

``` bash
./.scripts/reload-app.sh
```

The script detects the connected device, builds the APK and installs it.
If there are multiple devices it asks you which one to use.
If install fails due to a signature mismatch it uninstalls and retries automatically.

### Troubleshooting

If Gradle freezes or the device stops being detected:

``` bash
./.scripts/clean-processes.sh
```

This stops Gradle daemons, the Kotlin compiler daemon and restarts the ADB server.

## Environment Variables

Create a local.properties file:

``` properties
SUPABASE_URL=https://xxxx.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```

These values will be used once backend integration is enabled.

