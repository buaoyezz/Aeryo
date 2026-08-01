<div align="center">

<img src="./artwork/aeryo_app_icon.svg" alt="Aeryo" width="128">

<h1>Aeryo</h1>

<p>
一个简单、干净、舒适的网页浏览方式。
</p>

<p>
  <a href="#构建">构建</a>
  &nbsp;&middot;&nbsp;
  <a href="#模块">模块</a>
  &nbsp;&middot;&nbsp;
  <a href="#许可证">许可证</a>
  <br>
  <a href="./README.md">English Version</a>
</p>

</div>

<br>

<p align="center">
Aeryo 是一个基于 Jetpack Compose 和 Miuix 构建的 Android 浏览器<br>
它提供标签页浏览、搜索引擎切换、书签与历史记录、广告拦截、隐私控制以及下载功能
</p>


<a id="构建"></a>

<h2 align="center">
构建
</h2>

<p align="center">
项目使用 Gradle Wrapper 构建，需要 Android Studio 或已配置好的 Android SDK。
</p>

```powershell
.\gradlew.bat :app:assembleDebug
```

<p align="center">
Debug 构建不需要发布签名凭据。
</p>

> [!NOTE]
> 当本地存在 `keystore.properties` 文件时，将自动启用 Release 签名。
> 请使用 `keystore.properties.example` 作为模板，并妥善保管
> `keystore.properties` 文件以及 `signing/` 目录。


<a id="模块"></a>

<h2 align="center">
模块
</h2>

<div align="center">

<table>
  <tr>
    <td align="center">
      <b>app</b><br>
      应用主体、浏览器界面、<br>
      导航以及应用级状态管理
    </td>
    <td align="center">
      <b>core-browser</b><br>
      WebView、标签页、广告拦截、<br>
      以及浏览器核心服务
    </td>
  </tr>

  <tr>
    <td align="center">
      <b>feature-bookmarks</b><br>
      书签、历史记录以及私密<br>
      历史记录存储
    </td>
    <td align="center">
      <b>feature-downloads</b><br>
      下载请求、确认流程、<br>
      持久化以及用户界面
    </td>
  </tr>

  <tr>
    <td align="center">
      <b>feature-settings</b><br>
      Miuix 设置、隐私、<br>
      广告拦截以及关于页面
    </td>
    <td align="center">
    </td>
  </tr>
</table>

</div>


<a id="许可证"></a>

<h2 align="center">
许可证
</h2>

<p align="center">
<strong>GNU General Public License v3.0</strong>
</p>

<p align="center">
Aeryo 使用 GNU General Public License v3.0 许可证。
</p>


<h3 align="center">
第三方许可证
</h3>

<p align="center">
本项目使用了一些具有独立许可证的第三方库。
</p>

<p align="center">
<strong>Miuix</strong><br>
使用 Apache License 2.0 许可证。
</p>