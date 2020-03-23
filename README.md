# ExcuseMe
Because we don't need to be rude for ask permissions

[![CircleCI](https://circleci.com/gh/AraujoJordan/ExcuseMe.svg?style=shield)](https://circleci.com/gh/AraujoJordan/ExcuseMe)
[![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)](https://github.com/AraujoJordan/ExcuseMe/LICENSE)
[![Jitpack Enable](https://jitpack.io/v/AraujoJordan/ExcuseMe.svg)](https://jitpack.io/AraujoJordan/ExcuseMe/)


ExcuseMe is an Android library that provides an one-line implementation for android permissions made with Kotlin.

## Why use ExcuseMe?

1. Better performance with Kotlin Coroutines
   * A better performance in comparison with other libraries that use Thread as it uses less memory have better performance than threads for small async/ui thread changes.
2. One-line permission request
   * ExcuseMe could be used with Kotlin Suspending functions that gives a better syntax and better code readability
3. No more interface listeners to implement
   * With ExcuseMe, you don't need to implement callbacks interfaces that it just add boilerplate code to maintain
4. It can be used with lambda callbacks
   * If you don't know how to implement suspend functions, ExcuseMe could be used with Kotlin lambda callbacks
5. Polite way to ask for requests
   * Because we don't need to be rude for ask permissions

## Usage

### Simple one-line implementation
```kotlin
suspend fun requestToUseCamera() {
	val res = ExcuseMe.couldYouGive(this).permissionFor(android.Manifest.permission.CAMERA)
	if(res.granted.contains(android.Manifest.permission.CAMERA)) {
		//Do your naughty camera stuffs
	}
}
```
And that's it. No more Adapter implementations, ViewHolders and others boilerplate to maintain in your code.
The list of the example is a String, but you can use ANY type of objects instead.

### Kotlin Lambda Callback usage
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    ExcuseMe.couldYouGive(this).permissionFor(android.Manifest.permission.CAMERA) {
        if(it.granted.contains(android.Manifest.permission.CAMERA)) {
            //Do your naughty camera stuffs
        }
    }
}
```

## Installation

#### Step 1. Add the JitPack repository to your project build file 

```gradle
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

#### Step 2. Add the dependency to your app build file 

```gradle
dependencies {
	implementation 'com.github.AraujoJordan:ExcuseMe:0.0.1'
}
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

