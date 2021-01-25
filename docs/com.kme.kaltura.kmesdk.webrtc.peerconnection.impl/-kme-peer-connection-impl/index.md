---
title: KmePeerConnectionImpl - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.webrtc.peerconnection.impl](../index.html) / [KmePeerConnectionImpl](./index.html)

# KmePeerConnectionImpl

`class KmePeerConnectionImpl : `[`IKmePeerConnection`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection/index.html)`, `[`KmeSoundAmplitudeListener`](../../com.kme.kaltura.kmesdk.webrtc.stats/-kme-sound-amplitude-listener/index.html)

An implementation actions under WebRTC peer connection object

### Constructors

| [&lt;init&gt;](-init-.html) | `KmePeerConnectionImpl()`<br>An implementation actions under WebRTC peer connection object |

### Functions

| [addRemoteIceCandidate](add-remote-ice-candidate.html) | `fun addRemoteIceCandidate(candidate: IceCandidate?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Handle adding ICE candidate |
| [close](close.html) | `fun close(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Closes actual connection |
| [createAnswer](create-answer.html) | `fun createAnswer(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates an answer |
| [createOffer](create-offer.html) | `fun createOffer(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates an offers |
| [createPeerConnection](create-peer-connection.html) | `fun createPeerConnection(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, localVideoSink: VideoSink, remoteVideoSink: VideoSink, videoCapturer: VideoCapturer?, isPublisher: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, iceServers: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<IceServer>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates peer connection |
| [createPeerConnectionFactory](create-peer-connection-factory.html) | `fun createPeerConnectionFactory(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, events: `[`IKmePeerConnectionEvents`](../../com.kme.kaltura.kmesdk.webrtc.peerconnection/-i-kme-peer-connection-events/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Creates peer connection factory |
| [getRenderContext](get-render-context.html) | `fun getRenderContext(): Context?`<br>Getting rendering context for WebRTC |
| [onAmplitudeMeasured](on-amplitude-measured.html) | `fun onAmplitudeMeasured(bringToFront: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, amplitude: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Fired once sound amplitude measured |
| [removeRemoteIceCandidates](remove-remote-ice-candidates.html) | `fun removeRemoteIceCandidates(candidates: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<IceCandidate>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Handle remove remote ICE candidates |
| [setAudioEnabled](set-audio-enabled.html) | `fun setAudioEnabled(enable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Toggle audio |
| [setRemoteDescription](set-remote-description.html) | `fun setRemoteDescription(sdp: SessionDescription): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting remote SDP |
| [setVideoEnabled](set-video-enabled.html) | `fun setVideoEnabled(enable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Toggle video |
| [setVideoMaxBitrate](set-video-max-bitrate.html) | `fun setVideoMaxBitrate(maxBitrateKbps: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Change bitrate parameters of local video |
| [startVideoSource](start-video-source.html) | `fun startVideoSource(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Enable outgoing video stream |
| [stopVideoSource](stop-video-source.html) | `fun stopVideoSource(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Disable outgoing video stream |
| [switchCamera](switch-camera.html) | `fun switchCamera(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Switch between existing cameras |

### Companion Object Properties

| [AUDIO_TRACK_ID](-a-u-d-i-o_-t-r-a-c-k_-i-d.html) | `const val AUDIO_TRACK_ID: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

