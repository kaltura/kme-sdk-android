---
title: IKmePreferences.getString - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.prefs](../index.html) / [IKmePreferences](index.html) / [getString](./get-string.html)

# getString

`abstract fun getString(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultValue: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = ""): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`

Getting [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) value from the preferences by key

### Parameters

`key` - preferences key

`defaultValue` - default value in case key is not stored in the preferences

**Return**
stored value if exist, otherwise [defaultValue](get-string.html#com.kme.kaltura.kmesdk.prefs.IKmePreferences$getString(kotlin.String, kotlin.String)/defaultValue)

