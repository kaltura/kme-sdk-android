---
title: IKmeWebRTCController.addViewerPeerConnection - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.controller](../index.html) / [IKmeWebRTCController](index.html) / [addViewerPeerConnection](./add-viewer-peer-connection.html)

# addViewerPeerConnection

`abstract fun addViewerPeerConnection(requestedUserIdStream: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, renderer: `[`KmeSurfaceRendererView`](../../com.kme.kaltura.kmesdk.webrtc.view/-kme-surface-renderer-view/index.html)`, listener: `[`IKmePeerConnectionClientEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-client-events/index.html)`): `[`IKmePeerConnectionController`](../-i-kme-peer-connection-controller/index.html)`?`

Creates a viewer connection

### Parameters

`requestedUserIdStream` -

`renderer` - view for video rendering

`listener` - listener for p2p events

**Return**
viewer connection object as [IKmePeerConnectionController](../-i-kme-peer-connection-controller/index.html)

