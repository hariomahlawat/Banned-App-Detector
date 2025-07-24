# Banned App Detector

Offline Android app that scans for a predefined list of monitored packages (Facebook and its variants such as Messenger and Facebook Lite, Instagram, Truecaller) and reports if each is installed, disabled, or not present.

## Architecture

Originally planned as a multi module project, the sample has been collapsed into
a single `app` module for simplicity.

## How It Works

Tap Scan to enumerate monitored packages via PackageManager. Results are persisted locally in DataStore and shown in a Compose list with status chips.

## Privacy and Policy

No network permission. Only specific packages are declared in `<queries>` to avoid broad package visibility. No personal data leaves device.

## Extend Monitored List

Add entries to `MonitoredAppsRepositoryImpl`. Future improvement: load from JSON asset or remote config.

## Build

Requires Android Studio Iguana (2023.2.1 or newer) with JDK 17.

To build a debug version from the command line run:

```
./gradlew assembleDebug
```

Install the debug build on a connected device with:

```
./gradlew installDebug
```

For a production ready build run:

```
./gradlew assembleRelease
```

The release build type enables code and resource shrinking via R8/ProGuard.
Provide your own signing configuration before publishing to the Play Store.

You can also open the project in Android Studio and click **Run** to deploy.




## AI-based Risk Scanning

See [docs/AI-App-Scanning-Plan.md](docs/AI-App-Scanning-Plan.md) for a proposal on integrating an AI model to assess the risk of installed apps alongside the static banned list.
