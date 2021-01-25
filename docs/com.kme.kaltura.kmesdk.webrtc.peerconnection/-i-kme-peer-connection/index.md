---
title: IKmePeerConnection - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.webrtc.peerconnection](../index.html) / [IKmePeerConnection](./index.html)

# IKmePeerConnection

`interface IKmePeerConnection`

An interface for actions under WebRTC peer connection object

### Functions

| [addRemoteIceCandidate](add-remote-ice-candidate.html) | `abstract fun addRemoteIceCandidate(candidate: IceCandidate?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Handle adding ICE candidates |
| [close](close.html) | `abstract fun close(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Closes actual connection |
| [createAnswer](create-answer.html) | `abstract fun createAnswer(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates an answer |
| [createOffer](create-offer.html) | `abstract fun createOffer(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates an offers |
| [createPeerConnection](create-peer-connection.html) | `abstract fun createPeerConnection(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, localVideoSink: VideoSink, remoteVideoSink: VideoSink, videoCapturer: VideoCapturer?, isPublisher: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, iceServers: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<IceServer>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates peer connection |
| [createPeerConnectionFactory](create-peer-connection-factory.html) | `abstract fun createPeerConnectionFactory(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, events: `[`IKmePeerConnectionEvents`](../-i-kme-peer-connection-events/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates peer connection factory |
| [getRenderContext](get-render-context.html) | `abstract fun getRenderContext(): Context?`<br>Getting rendering context for WebRTC |
| [removeRemoteIceCandidates](remove-remote-ice-candidates.html) | `abstract fun removeRemoteIceCandidates(candidates: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<IceCandidate>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Handle remove remote ICE candidates |
| [setAudioEnabled](set-audio-enabled.html) | `abstract fun setAudioEnabled(enable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Toggle audio |
| [setRemoteDescription](set-remote-description.html) | `abstract fun setRemoteDescription(sdp: SessionDescription): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting remote SDP |
| [setVideoEnabled](set-video-enabled.html) | `abstract fun setVideoEnabled(enable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Toggle video |
| [startVideoSource](start-video-source.html) | `abstract fun startVideoSource(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Enable outgoing video stream |
| [stopVideoSource](stop-video-source.html) | `abstract fun stopVideoSource(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Disable outgoing video stream |
| [switchCamera](switch-camera.html) | `abstract fun switchCamera(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Switch between existing cameras |

### Inheritors

| [KmePeerConnectionImpl](../../com.kme.kaltura.kmesdk.webrtc.peerconnection.impl/-kme-peer-connection-impl/index.html) | `class KmePeerConnectionImpl : `[`IKmePeerConnection`](./index.html)`, `[`KmeSoundAmplitudeListener`](../../com.kme.kaltura.kmesdk.webrtc.stats/-kme-sound-amplitude-listener/index.html)<br>An implementation actions under WebRTC peer connection object |

