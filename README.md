<div align="center">

<img src="./artwork/aeryo_app_icon.svg" alt="Aeryo" width="128">

<h1>Aeryo</h1>

<p>
A simple, clean, and comfortable way to browse the web.
</p>

<p>
  <a href="#build">Build</a>
  &nbsp;&middot;&nbsp;
  <a href="#modules">Modules</a>
  &nbsp;&middot;&nbsp;
  <a href="#license">License</a>
  <br>
  🌏 |
  <a href="./README_ZH.md">简体中文</a>
  &nbsp;&middot;&nbsp;
  <a href="./README.md">English</a>
</p>


</div>

<br>

<p align="center">
Aeryo is an Android browser built with Jetpack Compose and Miuix.<br>
It brings tabbed browsing, search-engine switching, bookmarks and history,
ad blocking, privacy controls, and downloads into one focused browsing experience.
</p>


<a id="build"></a>

<h2 align="center">
Build
</h2>

<p align="center">
The project uses the Gradle wrapper and requires Android Studio or a configured
Android SDK.
</p>

```powershell
.\gradlew.bat :app:assembleDebug
```

<p align="center">
Debug builds do not require release credentials.
</p>

> [!NOTE]
> Release signing is enabled automatically when a local `keystore.properties`
> file is present. Use `keystore.properties.example` as a template, and keep
> `keystore.properties` and the `signing/` directory private.


<a id="modules"></a>

<h2 align="center">
Modules
</h2>

<div align="center">

<table>
  <tr>
    <td align="center">
      <b>app</b><br>
      Application shell, browser chrome,<br>
      navigation, and app-level state
    </td>
    <td align="center">
      <b>core-browser</b><br>
      WebView, tabs, ad-blocking,<br>
      and browser services
    </td>
  </tr>

  <tr>
    <td align="center">
      <b>feature-bookmarks</b><br>
      Bookmarks, history, and private<br>
      history storage
    </td>
    <td align="center">
      <b>feature-downloads</b><br>
      Download requests, confirmation,<br>
      persistence, and UI
    </td>
  </tr>

  <tr>
    <td align="center">
      <b>feature-settings</b><br>
      Miuix settings, privacy,<br>
      ad-blocking, and about pages
    </td>
    <td align="center">
    </td>
  </tr>
</table>

</div>


<a id="license"></a>

<h2 align="center">
License
</h2>

<p align="center">
<strong>GNU General Public License v3.0</strong>
</p>

<p align="center">
Aeryo is licensed under the GNU General Public License v3.0.
</p>


<h3 align="center">
Third-party Licenses
</h3>

<p align="center">
This project uses third-party libraries with their own licenses.
</p>

<p align="center">
<strong>Miuix</strong><br>
Licensed under the Apache License 2.0.
</p>
