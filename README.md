# sharinpixwebview-android

[![](https://jitpack.io/v/SharinPix/sharinpixwebview-android.svg)](https://jitpack.io/#SharinPix/sharinpixwebview-android)

## Steps to include SharinPixWebView in your project

In settings.gradle include this line:
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        .
        .
        .
        maven { url 'https://jitpack.io' }
    }
}
```

In build.gradle for your project module include this line:
```
dependencies {
    implementation 'com.sharinpix.webview:sharinpixwebview-android:LATEST_VERSION'
}
```
