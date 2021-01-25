---
title: KmeSlidesView - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.content.slides](../index.html) / [KmeSlidesView](./index.html)

# KmeSlidesView

`class KmeSlidesView : ConstraintLayout, `[`IKmeSlidesListener`](../-i-kme-slides-listener/index.html)

An implementation of slides view in the room

### Types

| [Config](-config/index.html) | `class Config` |

### Constructors

| [&lt;init&gt;](-init-.html) | `KmeSlidesView(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, attrs: `[`AttributeSet`](https://developer.android.com/reference/android/util/AttributeSet.html)`? = null, defStyleAttr: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0)`<br>An implementation of slides view in the room |

### Properties

| [currentSlide](current-slide.html) | `val currentSlide: `[`Slide`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/-active-content-payload/-slide/index.html)`?`<br>Getting actual slide |
| [size](size.html) | `val size: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Getting size of slides collection |

### Functions

| [init](init.html) | `fun init(config: `[`Config`](-config/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Initialize function. Setting config |
| [next](next.html) | `fun next(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Asking for the next slide form slides collection |
| [previous](previous.html) | `fun previous(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Asking for the previous slide form slides collection |
| [setSlides](set-slides.html) | `fun setSlides(slides: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Slide`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/-active-content-payload/-slide/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Set actual slides |
| [toSlide](to-slide.html) | `fun toSlide(slideNumber: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Getting slide by position from slides collection |

