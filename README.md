# Introduction
This project shows how to exercise the Google Places API surface. This project
contains sensitive information about API keys and Android debug certificates,
so this is a private repo.

# API keys

## 1. From Google API console
Create a new project and get an API key for Places API.
- [Google API Console](https://console.developers.google.com/flows/enableapi?apiid=placesandroid&reusekey=true)
    - Create a new key for your project (new or existing)
    - Copy the key and paste it in `strings.xml` in the `places_api_key` resource

## 2. Bind your app's SHA-1 fingerprint and package name
Bind this APK with the project in Google APIs console by creating an Android
restriction (this type of API key is required by Places API).

You will need:
  - Package name, in this case: `com.google.api.places.places_api_poc`
  - SHA1 fingerprint from your keystore, eg: `BB:0D:...:44:5D:75`

### ⚠️ Repeat this for every APK
Make sure to repeat this on every machine that you generate an APK from. For every
laptop or workstation that has a `~/.android` folder, you have to whitelist the
SHA1 code for that machine's keystore. If you don't do this, then Google Play Services
will throw an error like [this](https://stackoverflow.com/questions/47279161/runtimeexecutionexception-com-google-android-gms-common-api-apiexception-13-e).

# References

## Places API Documentation links
- [Get started w/ the Android API](https://developers.google.com/places/android-sdk/start)
- [Get Places API key](https://developers.google.com/places/android-sdk/signup)

# GMS API links
- [Tasks API](https://developers.google.com/android/guides/tasks)

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

## LiveData, ViewModel and Fragment
- [With fragments](https://developer.android.com/topic/libraries/architecture/viewmodel#sharing)
- [Fragment pitfalls](https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808)

## Android runtime permissions
- [Requesting dangerous permissions](https://developer.android.com/training/permissions/requesting)

## Android custom themes
- [Overriding default styles and themes](https://medium.com/@joannekao/android-working-with-themes-and-styles-18cde717f4d)

## Android UI
- [Bottom bar navigation](http://blog.iamsuleiman.com/using-bottom-navigation-view-android-design-support-library/)