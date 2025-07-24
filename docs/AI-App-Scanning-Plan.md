# AI-Based App Risk Evaluation Plan

This document proposes an extension to the project to provide AI-based suggestions
about potentially dangerous apps on the device. The goal is to augment the
existing static banned list with a data-driven approach that reduces false
positives and negatives.

## Overview

1. **Collect App Metadata** – For each installed app gather metadata such as
   package name, version, permissions, install/update timestamps, developer
   name, app store rating, and download count.
2. **Extract Signals** – Derive numerical features from the metadata:
   - Count of dangerous permissions requested.
   - Time since last update.
   - Popularity indicators (rating, reviews, installs).
   - Reputation of the developer (e.g. verified developer or known publisher).
3. **Risk Scoring Model** – Train a lightweight model (e.g. gradient boosted
   trees or shallow neural network). The model should predict a risk score
   between 0–100 based on the extracted signals.
4. **Explainability** – Provide explanations for the risk score by listing
   the contributing features (similar to how `RiskScoreCalculator` currently
   returns reasons).
5. **LLM Integration** – Optionally call a local or on-device language model to
   summarize recent user reviews and flag emerging concerns that might not be
   captured by numeric features.
6. **False Positive Reduction** – Calibrate the model using a large dataset of
   known safe popular apps to ensure that high‑profile apps with many
   permissions are not wrongly flagged.
7. **Display in Scan Result** – When the user scans, show both the banned‑list
   result and the AI risk assessment for each installed app. Include the
   explanation string so the user knows why an app was considered risky.

## Data Requirements

To build the model you would need a dataset containing:

- App permissions and metadata from the Play Store or other app sources.
- Historical labels indicating whether an app was removed from the store or is
  known to be malicious.
- Optional: user reviews or security reports summarised via an LLM.

## Implementation Steps

1. Add a background service that gathers app metadata on the device.
2. Feed the data into the `RiskScoreCalculator` and an additional ML model.
3. Cache results locally so the scan is quick and offline.
4. Update the UI to present the risk score alongside the monitored status.
5. Continuously refine the model with new data to keep detection robust.

