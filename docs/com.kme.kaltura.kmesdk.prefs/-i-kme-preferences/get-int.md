---
title: IKmePreferences.getInt - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.prefs](../index.html) / [IKmePreferences](index.html) / [getInt](./get-int.html)

# getInt

`abstract fun getInt(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultValue: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Getting [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) value from the preferences by key

### Parameters

`key` - preferences key

`defaultValue` - default value in case key is not stored in the preferences

**Return**
stored value if exist, otherwise [defaultValue](get-int.html#com.kme.kaltura.kmesdk.prefs.IKmePreferences$getInt(kotlin.String, kotlin.Int)/defaultValue)

