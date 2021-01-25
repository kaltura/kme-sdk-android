---
title: SlidesAdapter - kmeSdk
---

[kmeSdk](../../index.html) / [com.kme.kaltura.kmesdk.content.slides](../index.html) / [SlidesAdapter](./index.html)

# SlidesAdapter

`class SlidesAdapter : Adapter<`[`SlideHolder`](-slide-holder/index.html)`>`

### Types

| [SlideHolder](-slide-holder/index.html) | `inner class SlideHolder : ViewHolder` |

### Constructors

| [&lt;init&gt;](-init-.html) | `SlidesAdapter(cookie: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, filesUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?)` |

### Properties

| [cookie](cookie.html) | `val cookie: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [filesUrl](files-url.html) | `val filesUrl: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [onSlideClick](on-slide-click.html) | `var onSlideClick: (slide: `[`Slide`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/-active-content-payload/-slide/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Functions

| [getItemCount](get-item-count.html) | `fun getItemCount(): <ERROR CLASS>` |
| [onBindViewHolder](on-bind-view-holder.html) | `fun onBindViewHolder(holder: `[`SlideHolder`](-slide-holder/index.html)`, position: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateViewHolder](on-create-view-holder.html) | `fun onCreateViewHolder(parent: `[`ViewGroup`](https://developer.android.com/reference/android/view/ViewGroup.html)`, viewType: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`SlideHolder`](-slide-holder/index.html) |
| [setData](set-data.html) | `fun setData(data: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Slide`](../../com.kme.kaltura.kmesdk.ws.message.module/-kme-active-content-module-message/-active-content-payload/-slide/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

