# Introduction

This project shows how to exercise the Google Places API surface. This project
contains sensitive information about API keys and Android debug certificates,
so this is a private repo.

# API keys and such

## From Google API console

Create a new project and get an API key for Places API.

- `places-api-poc` project in Google APIs console
    - [Project under nazmul@fasterlap account](http://tinyurl.com/yb67bn2f)

- Places API key for `places-api-poc` project in Google Console
    - `AIzaSyDMffQ1RgvYFDAQlXymZZubfoS-lHXHjjU`

## Bind this APK to the Places API project

Bind this APK with the project in Google APIs console by creating an Android
restriction (this type of API key is required by Places API).

- Package name
    - `com.google.api.places.places_api_poc`

- Debug Cert on dev machine (rmbp13) SHA1 key
    - `21:9D:AC:80:E1:AB:3B:B7:2E:27:96:B0:2A:E5:21:D1:23:A7:D1:09`

# Misc

## Places API Documentation links
- [Get started w/ the Android API](https://developers.google.com/places/android-sdk/start)
- [Get Places API key](https://developers.google.com/places/android-sdk/signup)

## Android API Documentation links
- [Lifecycle awareness](https://developer.android.com/topic/libraries/architecture/lifecycle)
- [Use Anko](https://www.kotlindevelopment.com/why-should-use-anko/)

## Android UI layouts
- [Eg of LinearLayout w/ scrollview](http://tinyurl.com/yaht7rrm)
- [Eg of RelativeLayout options](http://tinyurl.com/y7rer3ch)

## Android Architecture Components
- [Gradle imports](https://developer.android.com/topic/libraries/architecture/adding-components)
- [Using LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
- [Code sample](https://github.com/nazmulidris/android_arch_comp_kt/tree/master/app/src/main/java/arch_comp_kt/nazmul/com)

## Android runtime permissions
- [Requesting dangerous permissions](https://developer.android.com/training/permissions/requesting)

## Android custom themes
- [Overriding default styles and themes](https://medium.com/@joannekao/android-working-with-themes-and-styles-18cde717f4d)