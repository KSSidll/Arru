<h1 align="center">Arru</h1>
<h2 align="center">Your expenses tracker</h2>

<br />

<p align="center">
  <img src="https://img.shields.io/badge/Api%2021+-50f270?logo=android&logoColor=black&style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Kotlin-a503fc?logo=kotlin&logoColor=white&style=for-the-badge"/>
  <img src="https://img.shields.io/static/v1?style=for-the-badge&message=Jetpack+Compose&color=4285F4&logo=Jetpack+Compose&logoColor=FFFFFF&label="/>
  <img src="https://img.shields.io/static/v1?style=for-the-badge&message=Material 3&color=lightblue&logoColor=333&logo=material-design&label="/>
</p>

<p align="center">
  <a href="https://apt.izzysoft.de/fdroid/index/apk/com.kssidll.arru">
    <img alt="IzzyOnDroid" src="https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/com.kssidll.arru"/>
  </a>
</p>

<h3 align="center">Arru is an app for expenditure tracking/analysis</h3>

<p align="middle">
    <img src="images/dashboard.jpg" width="30%"/>
    <img src="images/analysis.jpg" width="30%"/>
    <img src="images/transactions.jpg" width="30%"/>
    <img src="images/product_top.jpg" width="24%"/>
    <img src="images/categories_ranking.jpg" width="24%"/>
    <img src="images/merge.jpg" width="24%"/>
    <img src="images/backups.jpg" width="24%"/>
    <img src="images/transaction_add_item.jpg" width="30%"/>
    <img src="images/transaction_add_select.jpg" width="30%"/>
    <img src="images/transaction_add.jpg" width="30%"/>
</p>

<br/>

<p align="center">
  <a href="https://apt.izzysoft.de/fdroid/index/apk/com.kssidll.arru">
    <img alt="IzzyOnDroid" src="images/IzzyOnDroidButton.svg"/>
  </a>
</p>

# Features

- Light/Dark mode
- Wide screen support
- Local backups
- Data Export ([documentation](docs/export.md))
- Polish, English and partial Turkish localization
- Transaction baskets tracking your total expenditure with optional product, category, shop and producer spending tracking
- Comparisons between prices at different shops
- Ranking of categories and shops based on total money spent
- Merging capabilities for categories, shops, products and producers

# Tech Stack & Libraries

- [Kotlin](https://kotlinlang.org/) based

- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) for asynchronous computing

- [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) to emit values from data layer reactively

- [Hilt](https://dagger.dev/hilt/) for dependency injection

- [Compose Navigation Reimagined](https://github.com/olshevski/compose-navigation-reimagined) for animated navigation

- [Vico Compose](https://github.com/patrykandpatrick/vico) for graphs

- [Fuzzywuzzy](https://github.com/xdrop/fuzzywuzzy) for fuzzy searching capabilities

- Jetpack

  - [Compose](https://developer.android.com/jetpack/compose) - Modern Declarative UI style framework based on composable functions

  - [Room](https://developer.android.com/jetpack/androidx/releases/room) - Persistence library providing abstraction layer over SQLite

  - [Material You Kit](https://developer.android.com/jetpack/androidx/releases/compose-material3) - Material 3 powerful UI components

  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Manages UI-related data holder and lifecycle awareness. Allows data to survive configuration changes such as screen rotations

  - [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) - Observe Android lifecycles and handle UI states upon the lifecycle changes
