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

See [docs/AI-App-Scanning-Plan.md](docs/AI-App-Scanning-Plan.md) for a proposal on integrating an AI model to assess the risk of installed apps alongside the static banned list. The current implementation ships with a lightweight local sentiment analyser that estimates the ratio of negative reviews and factors it into the risk score.

## Permission & Risk Analysis Module

The app ships with an optional permission scanner located under
`com.hariomahlawat.bannedappdetector.permission`. It loads sensitive
permissions from JSON assets and categorises them as HIGH, MEDIUM or LOW
risk. Installed applications are scanned offline and each receives a
score based on the number of sensitive permissions requested. Packages
published by known Chinese-origin developers receive a small additional
risk bonus, while well known trusted packages are penalised to reduce
false positives.

From the home screen you can launch this scan via the **AI Scan** button,
which lists installed apps with their permission risk score.

System applications and services are automatically filtered out so the report
focuses on third‑party packages only. While scanning is in progress the screen
shows a loading indicator.

The asset files are stored in `src/main/assets`:

- `permissions.json` – mapping of permission to risk level
- `trusted_apps.json` – packages considered safe
- `chinese_publishers.json` – list of package name prefixes used to detect
  Chinese-origin apps
