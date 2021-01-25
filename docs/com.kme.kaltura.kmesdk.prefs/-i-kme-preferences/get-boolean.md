---
title: IKmePreferences.getBoolean - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.prefs](../index.html) / [IKmePreferences](index.html) / [getBoolean](./get-boolean.html)

# getBoolean

`abstract fun getBoolean(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultValue: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Getting [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) value from the preferences by key

### Parameters

`key` - preferences key

`defaultValue` - default value in case key is not stored in the preferences

**Return**
stored value if exist, otherwise [defaultValue](get-boolean.html#com.kme.kaltura.kmesdk.prefs.IKmePreferences$getBoolean(kotlin.String, kotlin.Boolean)/defaultValue)

