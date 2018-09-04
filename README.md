# Android app that tests Google Places API for Android
This project shows how to exercise the Google Places API surface.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [API keys](#api-keys)
  - [1. Get API key from Google API console](#1-get-api-key-from-google-api-console)
  - [2. Save your app's SHA-1 fingerprint and package name to Google API console](#2-save-your-apps-sha-1-fingerprint-and-package-name-to-google-api-console)
- [Implementation notes](#implementation-notes)
  - [Mysterious case of Snackbar animations not working on some devices](#mysterious-case-of-snackbar-animations-not-working-on-some-devices)
  - [Snackbar theming](#snackbar-theming)
    - [1. In XML](#1-in-xml)
    - [2. In code](#2-in-code)
- [References](#references)
  - [Places API Documentation links](#places-api-documentation-links)
  - [GMS API links](#gms-api-links)
  - [Android API Documentation links](#android-api-documentation-links)
  - [Android UI layouts](#android-ui-layouts)
  - [Android Architecture Components](#android-architecture-components)
  - [LiveData, ViewModel and Fragment](#livedata-viewmodel-and-fragment)
  - [Android runtime permissions](#android-runtime-permissions)
  - [Android custom themes](#android-custom-themes)
  - [Android UI](#android-ui)
  - [Material Design Android Theme colors](#material-design-android-theme-colors)
  - [Kotlin and higher order functions](#kotlin-and-higher-order-functions)
  - [AndroidX](#androidx)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

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

⚠️ Make sure to repeat this step on every machine that you generate an APK from. For every
laptop or workstation that has a `~/.android` folder, you have to whitelist the
SHA1 code for that machine's keystore. If you don't do this, then Google Play Services
will throw an error like [this](http://tinyurl.com/y8hwnegt).

# Implementation notes

## Mysterious case of Snackbar animations not working on some devices
While using the design support library, I ran into the issue of Snackbar animations
not occurring on certain devices I tested this app on (I tested it on Pixel XL, Pixel 2, and 
Pixel 2XL). The devices that were running Lastpass had a strange behavior of Snackbars not 
animation when they were shown (they are supposed to animation and move the FAB out of the way 
when they appear and disappear).

Turns out the reason for this is that when Accessibility Services (Talkback) is turned on,
this triggers a bug in the Snackbar codebase, which prevents the animation from showing.
1. [More info on SO](https://stackoverflow.com/a/37233527/2085356)
1. [🕷 The bug](https://issuetracker.google.com/issues/37092972#c2)

The current solution is to use M2 (Material Design 2) components for Android, which are in
alpha (as of Sep 2, 2018). To get started w/ M2 for Android, here are some links. It looks
promising, as there are plans to address theming along with quite a few bugs.

1. [Material Design 2 Components for Android, Getting Started](http://tinyurl.com/yak2s4jo)
1. [Material Design 2 Components for Android, Theming](http://tinyurl.com/y7rs6f6z)

## Snackbar theming
With the current implementation of the Design Support lib (and the Material Design Components
AndroidX library) there's a [🕷 bug in Snackbar](https://issuetracker.google.com/issues/37120757)
that hardcodes the background and text colors, instead of inheriting them from the current theme.
This is planned to be addressed in the future with the [revamp of 
theming](http://tinyurl.com/yb5equps) coming soon in Material Design Components (AndroidX library).

In the meantime, there are 2 workarounds, one in XML and one in code.

### 1. In XML
If you take a look at the [source code for `theme_base.xml` file](http://tinyurl.com/y8eex34w) 
that's defined in the Android Material Components (AndroidX library), and search for the 
`<!-- Widget styles -->` comment, you will find the `snackbarStyle` item that's defined there.

This is the style that we have to override in order to get our app's theme to be applied to
the Snackbar widget (without having to write any extra code to style our specific Snackbar
widget as is shown in the next section). 

Also, this technique can be used to override any default
widget styles (do this with `materialButtonStyle` to change the default style of all Material
Button components).

1. Define the new style that will override the existing style.

    ```xml
    <style name="AppSnackbarStyle" parent="Widget.MaterialComponents.Snackbar">
        <item name="android:background">@color/colorPrimary</item>
    </style>
    ```

2. In your theme, make sure to apply this new style (`AppSnackbarStyle`) to the theme's
   `snackbarStyle` element (you're telling your theme to use your style instead of default).
   This is the style that's used by `Snackbar` in order to paint itself. Again, this is
   [planned to be fixed in the future](http://tinyurl.com/yb5equps), since you shouldn't have 
   to perform this step at all, and the Snackbar should really inherit items like 
   `colorPrimary`, etc that are defined in your theme.

    ```xml
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.MaterialComponents">
        ...
        <item name="snackbarStyle">@style/AppSnackbarStyle</item>
    </style>
    ```

### 2. In code
The code below shows the approach of overriding the Snackbar's background color and action
text color w/ the desired color values (which are loaded from the currently applied theme).

```kotlin
object ThemedSnackbar {

    fun show(containerView: View, message: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(containerView, message, duration).apply {
            setActionTextColor(
                getFromTheme(containerView.context, android.R.attr.textColorPrimary)
            )
            view.setBackgroundColor(
                getFromTheme(containerView.context, android.R.attr.colorPrimary)
            )
        }.show()
    }

    fun show(containerView: View, resId: Int, duration: Int) {
        show(containerView, containerView.resources.getText(resId), duration)
    }

    private fun getFromTheme(context: Context, resId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, true)
        return typedValue.data
    }

}
```
# References

## Places API Documentation links
- [Get started w/ the Android API](https://developers.google.com/places/android-sdk/start)
- [Get Places API key](https://developers.google.com/places/android-sdk/signup)

## GMS API links
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
- [Code sample](http://tinyurl.com/ya5q3cpq)

## LiveData, ViewModel and Fragment
- [With fragments](https://developer.android.com/topic/libraries/architecture/viewmodel#sharing)
- [Fragment pitfalls](http://tinyurl.com/yc9wm6k6)

## Android runtime permissions
- [Requesting dangerous permissions](https://developer.android.com/training/permissions/requesting)

## Android custom themes
- [Overriding default styles and themes](http://tinyurl.com/y7tk2eap)

## Android UI
- [Bottom bar navigation](http://tinyurl.com/y7dhzhgj)
- [CoordinatorLayout and FAB](http://tinyurl.com/yanogrql)
- [FAB](https://developer.android.com/guide/topics/ui/floating-action-button)

## Material Design Android Theme colors
- [Android color theming Material Design](https://material.io/develop/android/theming/color/)
- [Material Design color picker tool](https://material.io/tools/color/#!/)
- [Applying theme colors to Snackbar](http://tinyurl.com/yb2vnxau)

## Kotlin and higher order functions
- [Passing functions as arguments](http://tinyurl.com/y925dr7c)

## AndroidX
- [Migration guide to AndroidX](http://tinyurl.com/y74uob3s)
- [Blog post](https://android-developers.googleblog.com/2018/05/hello-world-androidx.html)

## GSON
- [GSON User Guide](https://github.com/google/gson/blob/master/UserGuide.md)
- [GSON API docs](https://google.github.io/gson/apidocs/)
- [GSON pretty print](https://www.mkyong.com/java/how-to-enable-pretty-print-json-output-gson/)

## Modal Bottom Sheets (BottomSheetDialogFragment)

- [MDC Android doc](http://tinyurl.com/ydgvspm4)
- [MDC Android src](http://tinyurl.com/y7q7loxm)
- [BottomSheetDialogFragment tutorial](http://tinyurl.com/y76vyeks)