---
title: IKmePreferences - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.prefs](../index.html) / [IKmePreferences](./index.html)

# IKmePreferences

`interface IKmePreferences`

An interface for the preferences storage

### Functions

| [clear](clear.html) | `abstract fun clear(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Clears all stored preferences |
| [clearCurrentPrefs](clear-current-prefs.html) | `abstract fun clearCurrentPrefs(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Clears preference row by key value |
| [getBoolean](get-boolean.html) | `abstract fun getBoolean(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultValue: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Getting [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) value from the preferences by key |
| [getInt](get-int.html) | `abstract fun getInt(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultValue: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Getting [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) value from the preferences by key |
| [getLong](get-long.html) | `abstract fun getLong(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultValue: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = 0L): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>Getting [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) value from the preferences by key |
| [getString](get-string.html) | `abstract fun getString(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultValue: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = ""): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`<br>Getting [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) value from the preferences by key |
| [putBoolean](put-boolean.html) | `abstract fun putBoolean(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, value: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Put [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) value to the preferences |
| [putInt](put-int.html) | `abstract fun putInt(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Put [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) value to the preferences |
| [putLong](put-long.html) | `abstract fun putLong(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, value: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Put [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) value to the preferences |
| [putString](put-string.html) | `abstract fun putString(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Put [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) value to the preferences |

