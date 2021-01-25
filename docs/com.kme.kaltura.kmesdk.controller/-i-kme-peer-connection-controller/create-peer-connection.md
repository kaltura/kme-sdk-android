---
title: IKmePeerConnectionController.createPeerConnection - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmePeerConnectionController](index.html) / [createPeerConnection](./create-peer-connection.html)

# createPeerConnection

`abstract fun createPeerConnection(isPublisher: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, listener: `[`IKmePeerConnectionClientEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-client-events/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Creates p2p connection

### Parameters

`isPublisher` - indicates type of connection

`requestedUserIdStream` - id of a stream

`listener` - listener for p2p events