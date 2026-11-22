# Trackium Lite Android

Trackium Lite is a standalone Android app (`tech.trackium.lite`, Android 9 and later). It opens the first-party HTTPS Gateway workspace in a restricted Android WebView. It does not share the Core app’s storage, runtime, IPC, signing or release pipeline.

## Build and install

Use JDK 21, Android SDK platform 36 and the repository’s Gradle wrapper. From the repository root:

```sh
./gradlew -p lite --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
adb install -r lite/app/build/outputs/apk/debug/app-debug.apk
```

Keep the supplied debug signing certificate for upgrades. An APK signed with a different certificate cannot update the same application ID in place.

## Connect and pair

Enter the exact HTTPS Gateway origin with a publicly or device-trusted certificate, matching the Gateway’s configured public origin. Lite loads `/app/` on that origin with same-origin Gateway API requests. Enter the tenant UUID, obtain owner approval, then complete the challenge in the app. Devices, shipments, history and Integritas test proof status use the Gateway workspace. Export evidence opens selectable JSON in the app.

Use **Disconnect and revoke** in the workspace settings to revoke the server session. **Forget Gateway and local session** clears the app’s local cookies and WebView storage. Changing Gateway clears the same data before loading the new origin. After an error, reopen the configured Gateway to recheck the session. A revoked or expired session needs fresh approval. The Android host stores the origin and lets the WebView and first-party Gateway manage the existing HttpOnly cookies. It passes no bearer key or Minima data across that boundary.

For device acceptance, pair on an approved HTTPS staging Gateway and exercise rotation, background/resume, process death, offline recovery, tenant switch, logout, proof recheck and export. Use test proofs and disposable tenants.
