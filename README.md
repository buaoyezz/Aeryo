# Aeryo Browser

Aeryo is a Miuix-styled Android browser built with Jetpack Compose. It combines
tabbed browsing, search-engine switching, bookmarks and history, ad blocking,
privacy controls, and a download manager with system or built-in download modes.

## Build

The project uses the Gradle wrapper and requires Android Studio or a configured
Android SDK. Debug builds do not require release credentials:

```powershell
.\gradlew.bat :app:assembleDebug
```

Release signing is enabled automatically when a local `keystore.properties`
file is present. Keep that file and the `signing/` directory private.

## Modules

- `app`: application shell, browser chrome, navigation, and app-level state
- `core-browser`: WebView, tabs, ad-blocking, and browser services
- `feature-bookmarks`: bookmarks, history, and private history storage
- `feature-downloads`: download requests, confirmation, persistence, and UI
- `feature-settings`: Miuix settings, privacy, ad-blocking, and about pages

## License

This repository is prepared for the project owner. Add the project license
before public redistribution.
> [!RELEASESIGNING]
> + Release signing is enabled automatically when a local `keystore. 
> properties`
> file is present. Use `keystore.properties.example` as a 
> template, and keep
> `keystore.properties` and the `signing/` directory private.
