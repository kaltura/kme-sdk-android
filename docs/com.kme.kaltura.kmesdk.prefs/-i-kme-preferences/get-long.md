---
title: IKmePreferences.getLong - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.prefs](../index.html) / [IKmePreferences](index.html) / [getLong](./get-long.html)

# getLong

`abstract fun getLong(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultValue: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = 0L): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)

Getting [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) value from the preferences by key

### Parameters

`key` - preferences key

`defaultValue` - default value in case key is not stored in the preferences

**Return**
stored value if exist, otherwise [defaultValue](get-long.html#com.kme.kaltura.kmesdk.prefs.IKmePreferences$getLong(kotlin.String, kotlin.Long)/defaultValue)

