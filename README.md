# Introduction
This project shows how to exercise the Google Places API surface.

# API keys

## 1. Get API key from Google API console
Create a new project and get an API key for Places API.
- [Google API Console](https://console.developers.google.com/flows/enableapi?apiid=placesandroid&reusekey=true)
    - Create a new key for your project (new or existing)
    - Copy the key and paste it in `strings.xml` in the `places_api_key` resource

## 2. Save your app's SHA-1 fingerprint and package name to Google API console
Bind this APK with the project in Google APIs console by creating an Android
restriction (this type of API key is required by Places API).

- You will need:
    - Package name, in this case: `com.google.api.places.places_api_poc`
    - SHA1 fingerprint from your keystore, eg: `BB:0D:...:44:5D:75`

### ⚠️ Repeat this for every APK
Make sure to repeat this on every machine that you generate an APK from. For every
laptop or workstation that has a `~/.android` folder, you have to whitelist the
SHA1 code for that machine's keystore. If you don't do this, then Google Play Services
will throw an error like [this](https://stackoverflow.com/questions/47279161/runtimeexecutionexception-com-google-android-gms-common-api-apiexception-13-e).

# Mysterious case of Snackbar animations not working on some devices

I ran into the issue of Snackbar animations not occurring on certain devices I tested this
app on (I tested it on Pixel XL, Pixel 2, and Pixel 2XL). The devices that were running
Lastpass had a strange behavior of Snackbars not animation when they were shown (they are
supposed to animation and move the FAB out of the way when they appear and disappear).

Turns out the reason for this is that when Accessibility Services (Talkback) is turned on,
this triggers a bug in the Snackbar codebase, which prevents the animation from showing.
1. [More info on SO](https://stackoverflow.com/a/37233527/2085356)
1. [The bug](https://issuetracker.google.com/issues/37092972#c2)

The current solution is to use M2 (Material Design 2) components for Android, which are in
alpha (as of Sep 2, 2018). To get started w/ M2 for Android, here are some links. It looks
promising, as themeing is getting fixed along with quite a few bugs.

1. [Material Design 2 Components for Android, Getting Started](http://tinyurl.com/yak2s4jo)
1. [Material Design 2 Components for Android, Theming](http://tinyurl.com/y7rs6f6z)

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
- [CoordinatorLayout and FAB](https://www.androidauthority.com/using-coordinatorlayout-android-apps-703720/)
- [FAB](https://developer.android.com/guide/topics/ui/floating-action-button)

## Material Design Android Theme colors
- [Android color theming Material Design](https://material.io/develop/android/theming/color/)
- [Material Design color picker tool](https://material.io/tools/color/#!/)
- [Applying theme colors to Snackbar](https://www.viralandroid.com/2015/08/android-snackbar-design-support-library.html)

## Kotlin and higher order functions
- [Passing functions as arguments](https://discuss.kotlinlang.org/t/how-to-pass-a-function-as-parameter-to-another/848)