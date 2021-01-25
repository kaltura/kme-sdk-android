---
title: IKmeSlidesListener - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.content.slides](../index.html) / [IKmeSlidesListener](./index.html)

# IKmeSlidesListener

`interface IKmeSlidesListener`

An interface for slides in the room

### Properties

| [currentSlide](current-slide.html) | `abstract val currentSlide: `[`Slide`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/-active-content-payload/-slide/index.html)`?`<br>Getting actual slide |
| [size](size.html) | `abstract val size: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Getting size of slides collection |

### Functions

| [init](init.html) | `abstract fun init(config: `[`Config`](../-kme-slides-view/-config/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Initialize function. Setting config |
| [next](next.html) | `abstract fun next(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Asking for the next slide form slides collection |
| [previous](previous.html) | `abstract fun previous(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Asking for the previous slide form slides collection |
| [setSlides](set-slides.html) | `abstract fun setSlides(slides: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Slide`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/-active-content-payload/-slide/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Set actual slides |
| [toSlide](to-slide.html) | `abstract fun toSlide(slideNumber: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Getting slide by position from slides collection |

### Inheritors

| [KmeSlidesView](../-kme-slides-view/index.html) | `class KmeSlidesView : ConstraintLayout, `[`IKmeSlidesListener`](./index.html)<br>An implementation of slides view in the room |

