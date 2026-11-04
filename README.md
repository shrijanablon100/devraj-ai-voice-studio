# Devraj AI Voice Studio — starter project

This is the first Android UI prototype for the project.

## What works in this starter
- Android app shell and Nepali UI
- Pick an audio sample from the phone
- Enter a Nepali script
- Basic validation and status messages

## Not yet implemented
This starter does **not** clone voices or generate speech yet. A real voice-cloning backend/API must be selected, configured, secured, and connected before the generate button can produce audio. No voice sample is uploaded by this prototype.

## Build an APK with GitHub Actions
1. Create a GitHub repository named `devraj-ai-voice-studio`.
2. Upload all files and folders from this project to the repository root.
3. Open the repository's **Actions** tab and enable Actions if prompted.
4. Run **Build Android APK** (or push to the `main` branch).
5. Open the completed workflow run and download the `Devraj-AI-Voice-Studio-debug` artifact.
6. Extract the ZIP and install `app-debug.apk` on your Android phone. You may need to allow installation from your browser/files app.

This is a debug APK for testing, not a Play Store release.
