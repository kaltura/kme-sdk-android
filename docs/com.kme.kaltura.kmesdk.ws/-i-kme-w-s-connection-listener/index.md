---
title: IKmeWSConnectionListener - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.ws](../index.html) / [IKmeWSConnectionListener](./index.html)

# IKmeWSConnectionListener

`interface IKmeWSConnectionListener`

An interface for socket connection events

### Functions

| [onClosed](on-closed.html) | `abstract fun onClosed(code: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, reason: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Event about socket connection closed |
| [onClosing](on-closing.html) | `abstract fun onClosing(code: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, reason: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Event about socket connection closing |
| [onFailure](on-failure.html) | `abstract fun onFailure(throwable: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Event about socket failure |
| [onOpen](on-open.html) | `abstract fun onOpen(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Event about socket connection opened |

