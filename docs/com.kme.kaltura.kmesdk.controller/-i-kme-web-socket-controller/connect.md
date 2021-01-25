---
title: IKmeWebSocketController.connect - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeWebSocketController](index.html) / [connect](./connect.html)

# connect

`abstract fun connect(url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, companyId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, roomId: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, isReconnect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, token: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, listener: `[`IKmeWSConnectionListener`](../../com.kme.kaltura.kmesdk.ws/-i-kme-w-s-connection-listener/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Establish socket connection

### Parameters

`url` - url of a destination

`companyId` - id of a company

`roomId` - id of a room

`isReconnect` - reconnection flag

`token` - room auth token

`listener` - connection listener