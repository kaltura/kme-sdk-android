---
title: IKmeMediaPlaybackListener - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.content.playkit](../index.html) / [IKmeMediaPlaybackListener](./index.html)

# IKmeMediaPlaybackListener

`interface IKmeMediaPlaybackListener`

An interface for media files playback

### Properties

| [currentPosition](current-position.html) | `abstract val currentPosition: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>Getting current playing position |
| [duration](duration.html) | `abstract val duration: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>Getting duration of current media file |

### Functions

| [addListener](add-listener.html) | `abstract fun <E : PKEvent?> addListener(groupId: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`, type: `[`Class`](https://developer.android.com/reference/java/lang/Class.html)`<`[`E`](add-listener.html#E)`>, listener: Listener<`[`E`](add-listener.html#E)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Adding listener for specific event`abstract fun addListener(groupId: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`, type: `[`Enum`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)`<*>, listener: Listener<*>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Adding event listener |
| [init](init.html) | `abstract fun init(config: `[`Config`](../-kme-media-view/-config/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Init media view |
| [isEnded](is-ended.html) | `abstract fun isEnded(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check is playback of media file is ended |
| [pause](pause.html) | `abstract fun pause(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Pause playback |
| [play](play.html) | `abstract fun play(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Start playback |
| [removeListeners](remove-listeners.html) | `abstract fun removeListeners(groupId: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Removing event listener |
| [replay](replay.html) | `abstract fun replay(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Replay playback |
| [seekTo](seek-to.html) | `abstract fun seekTo(seekTo: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Seek to position |
| [setMedia](set-media.html) | `abstract fun setMedia(url: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setting media file url |

### Inheritors

| [KmeMediaView](../-kme-media-view/index.html) | `class KmeMediaView : `[`FrameLayout`](https://developer.android.com/reference/android/widget/FrameLayout.html)`, `[`IKmeMediaPlaybackListener`](./index.html)<br>An implementation of view for media files playback |

