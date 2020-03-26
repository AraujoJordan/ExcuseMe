# ExcuseMe
Because we don't need to be rude for asking permissions

[![CircleCI](https://circleci.com/gh/AraujoJordan/ExcuseMe.svg?style=shield)](https://circleci.com/gh/AraujoJordan/ExcuseMe)
[![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)](https://github.com/AraujoJordan/ExcuseMe/LICENSE)
[![Jitpack Enable](https://jitpack.io/v/AraujoJordan/ExcuseMe.svg)](https://jitpack.io/AraujoJordan/ExcuseMe/)

ExcuseMe is an Android library that provides an one-line implementation for android permissions made with Kotlin.

## Why you should use ExcuseMe?

1. Better performance with Kotlin Coroutines
   * A better performance in comparison with other libraries that use Thread as it uses less memory have better performance than threads for small async/ui thread changes.
2. One-line permission request
   * ExcuseMe can be used with Kotlin Suspending functions that gives a better syntax and better code readability
3. No more interface listeners to implement
   * With ExcuseMe, you don't need to implement callbacks interfaces that it just add boilerplate code to maintain
4. It can be used with lambda callbacks
   * If you don't know how to implement suspend functions, ExcuseMe could be used with Kotlin lambda callbacks
5. Polite way to ask for requests
   * Because we don't need to be rude for asking permissions

## Usage

ExcuseMe is easier way to implement Permissions in Android. But you still have to add the permission in the AndroidManifest.xml file.

![AndroidManifest.xml location](/doc/manifestLocation.webp "AndroidManifest.xml location")

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="your.package.name">

    <!-- Add you permission here-->
    <uses-permission android:name="android.permission.CAMERA" />
    
    <application
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
        ...
    </application>
</manifest>
```

You can find the complete list of permissions on [this documentation](https://developer.android.com/reference/android/Manifest.permission "Manifest Permissions")
And than, you can use on of the two implementations bellow to add the permission properly:

1. [The Simple one-line Usage](#onelineusage)
2. [Kotlin Lambda Callback Usage](#lambdacallback)


### <a name='onelineusage'></a>Simple one-line Usage

This implementation uses [suspend functions](https://kotlinlang.org/docs/reference/coroutines/composing-suspending-functions.html#composing-suspending-functions "Composing Suspending Functions") to make it easier the permission request. It will listen async the permission dialog response, so it won't pause the UI.

```kotlin
suspend fun cameraUsage() {
	val res = ExcuseMe.couldYouGive(this).permissionFor(android.Manifest.permission.CAMERA)
	if(res.granted.contains(android.Manifest.permission.CAMERA)) {
		//Do your camera stuffs
	}
}
```
And that's it. No more override the onRequestPermissionsResult() implementation on your activity, no more class-scope variables to keep what you want for the permission result, and no more others boilerplate to maintain in your code.

If you want to learn more of how to use Suspend functions, I recommend [this video](https://www.youtube.com/watch?v=IQf-vtIC-Uc "Android Developers") to understand it.

### <a name='lambdacallback'></a>Kotlin Lambda Callback Usage

This implementation uses [trailing lambdas callbacks](https://kotlinlang.org/docs/reference/lambdas.html#passing-a-lambda-to-the-last-parameter "Passing trailing lambdas"), so it will be natural like as an OnClickListener implementation. 

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    ExcuseMe.couldYouGive(this).permissionFor(
        android.Manifest.permission.CAMERA,
        ) {
        if(it.granted.contains(android.Manifest.permission.CAMERA)) {
            //Do your camera stuffs
        }
    }
}
```

This method doesn't need to use a suspend function, but it uses callback.

## Requesting multiple permissions

You can also run multiple permissions request in the same function and syntax.

```kotlin
suspend fun lotOfPermissions() {
	val res = ExcuseMe.couldYouGive(this).permissionFor(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
}
```


## Installation

#### Step 1. Add the JitPack repository to your project build file 

+ build.gradle (Project: YourProjectName)
```gradle
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

#### Step 2. Add the dependency to your app build file 

+ build.gradle (Module: app) [![Jitpack Enable](https://jitpack.io/v/AraujoJordan/ExcuseMe.svg)](https://jitpack.io/AraujoJordan/ExcuseMe/)
```gradle
dependencies {
	implementation 'com.github.AraujoJordan:ExcuseMe:x.x.x'
}
```

And that's it!

## Extras

ExcuseMe is in-built with simple functions that helps user with permissions related problems.

### Checking granted permissions

You can use this method to check one or multiple permissions in one simple function call

```kotlin
val bool = ExcuseMe.doWeHavePermissionFor(this, android.Manifest.permission.CAMERA)

//You can also ask if the system have multiple permissions (Can be more than two)
val bool = ExcuseMe.doWeHavePermissionFor(this,
    android.Manifest.permission.CAMERA,
    android.Manifest.permission.READ_CONTACTS,
    ...
    )
```




## License
```
MIT License

Copyright (c) 2020 Jordan L. A. Junior

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

