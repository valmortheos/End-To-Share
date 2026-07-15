# 📐 Android Development Agent Rulebook
### Strict Guidelines untuk AI Coding Agent (Jules) — Versi Senior Multi-Disiplin

> Dokumen ini WAJIB diikuti secara **strict, tanpa pengecualian**, oleh AI agent yang menulis kode untuk proyek ini. Setiap deviasi dari rule di bawah ini dianggap sebagai bug, bukan preferensi.

---

## 0. Peran yang Harus Disimulasikan Agent

Saat menulis kode, agent harus berpikir dan bertindak sekaligus sebagai:

- **Senior Android Engineer** — paham lifecycle, performance, memory, API level behavior.
- **Senior UI/UX Designer** — paham hierarki visual, spacing, motion, aksesibilitas.
- **Senior Interface Designer (Material Design 3 / Material You)** — paham token warna dinamis, elevation, shape system.
- **Release Engineer** — paham signing, build variant, CI/CD GitHub Actions.

Jika salah satu peran ini diabaikan, hasil akan terasa "amatir" — dan itu **tidak diterima**.

> 📎 **Dokumen pendamping**: `AGENTS.md` di root repo yang sama berisi detail implementasi navigasi (Navigation Component, Bottom Nav, Top Tabs), visual hierarchy, ikonografi/SVG, dan haptic dengan kode siap pakai. Kalau ada konflik: dokumen ini (`ANDROID_AGENT_RULES.md`) menang untuk arsitektur data/DI/build/signing; `AGENTS.md` menang untuk detail navigasi UI dan desain visual. Baca keduanya sebelum mulai kerja.

---

## 1. Spesifikasi Proyek (Non-Negotiable)

| Item | Requirement |
|---|---|
| `minSdk` | 31 (Android 12) |
| `targetSdk` | 36 (Android 16) |
| `compileSdk` | 36 |
| Bahasa | Kotlin 100% (tidak ada Java) |
| UI Toolkit | **Android View System (XML layout)** — bukan Jetpack Compose. Gunakan `View Binding` (bukan `findViewById` manual, bukan `synthetic`) |
| Build System | Gradle Kotlin DSL (`build.gradle.kts`) |
| Arsitektur | MVVM + Clean Architecture (`data` / `domain` / `presentation`) |
| DI | Hilt |
| Distribusi | **HANYA** lewat GitHub Actions Artifacts (APK ter-sign). **Tidak ada Play Store, tidak ada distribusi manual.** |

---

## 2. 🚫 Aturan Wajib: Edge-to-Edge & Status Bar (Pelanggaran Paling Sering Terjadi)

Ini adalah sumber utama kenapa aplikasi terasa "amatir". Agent **WAJIB** mengikuti pola ini secara persis:

### 2.1 Aktifkan Edge-to-Edge Sejak Awal
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // WAJIB dipanggil SEBELUM super.onCreate() dan setContentView()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
```

### 2.2 Larangan Mutlak
- ❌ **DILARANG** mengatur warna status bar / navigation bar secara manual lewat `Window.statusBarColor` dengan warna solid yang "ditempel" — ini menyebabkan status bar terlihat "terpisah" dari body aplikasi.
- ❌ **DILARANG** menggunakan `windowFullscreen` lama atau `SYSTEM_UI_FLAG_*` (deprecated API 30 ke bawah, termasuk `View.SYSTEM_UI_FLAG_LAYOUT_STABLE` dkk).
- ❌ **DILARANG** memberi background berbeda antara area status bar dan konten (misalnya `<View>` warna solid ditaruh manual di atas layout sebagai "status bar palsu") — keduanya harus terasa **satu kanvas visual yang menyatu**, termasuk di area notch / cutout.
- ❌ **DILARANG** memberi `android:fitsSystemWindows="true"` pada root layout jika root tersebut seharusnya menyatu penuh dengan status bar (`fitsSystemWindows` justru memaksa padding otomatis yang membuat efek "terpisah" itu terjadi lagi).

### 2.3 Pola yang Benar
- Status bar dan navigation bar harus **transparan** secara default (hasil dari `WindowCompat.setDecorFitsSystemWindows(window, false)`).
- Root layout XML (`ConstraintLayout`/`CoordinatorLayout` utama) harus mengalir **di bawah** status bar & notch. JANGAN set `fitsSystemWindows="true"` di root. Gunakan `ViewCompat.setOnApplyWindowInsetsListener` untuk memberi padding HANYA pada elemen yang butuh (misalnya `Toolbar`/`AppBarLayout`), BUKAN pada seluruh background:
```kotlin
ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
    val systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
    view.updatePadding(top = systemBars.top)
    insets
}

ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavOrFab) { view, insets ->
    val systemBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
    view.updatePadding(bottom = systemBars.bottom)
    insets
}
```
- Warna ikon status bar (terang/gelap) harus otomatis mengikuti tema aplikasi (light/dark) menggunakan:
```kotlin
val isDark = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
val controller = WindowCompat.getInsetsController(window, window.decorView)
controller.isAppearanceLightStatusBars = !isDark
controller.isAppearanceLightNavigationBars = !isDark
```
- Untuk perangkat dengan notch/cutout (punch-hole, waterfall): pastikan `manifest` punya:
```xml
<meta-data
    android:name="android.max_aspect"
    android:value="2.4" />
```
dan di `themes.xml`:
```xml
<style name="Theme.App" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="android:windowLayoutInDisplayCutoutMode">shortEdges</item>
    <item name="android:statusBarColor">@android:color/transparent</item>
    <item name="android:navigationBarColor">@android:color/transparent</item>
    <item name="android:windowLightStatusBar">true</item>
</style>
```
- JANGAN pasang padding manual dengan `android:paddingTop="24dp"` di root layout sebagai "perkiraan tinggi status bar" — ini hardcode yang rusak di device berbeda. Selalu gunakan `WindowInsetsCompat` yang membaca ukuran insets sesungguhnya per device.

### 2.4 Checklist Validasi Visual
Agent harus memverifikasi (via code review sendiri sebelum submit):
- [ ] Background aplikasi terlihat menyambung mulus dari notch sampai navigation bar.
- [ ] Tidak ada garis/blok warna solid yang "memotong" area status bar dari body.
- [ ] Icon status bar otomatis kontras terhadap background (tidak pernah putih-di-atas-putih atau hitam-di-atas-hitam).
- [ ] Gesture navigation bar (3-button atau gesture) tidak menutupi tombol interaktif (FAB, bottom bar) — gunakan `ViewCompat.setOnApplyWindowInsetsListener` pada elemen tersebut saja, bukan `fitsSystemWindows` global.
- [ ] Tidak ada nilai padding/margin hardcode yang meniru tinggi status bar/navigation bar.

---

## 3. Material Design 3 — Bukan Sekadar "Extend Theme.Material3"

Banyak agent berhenti di "extend `Theme.Material3.DayNight`" di `themes.xml` tapi tidak benar-benar mengimplementasi M3 dengan benar di XML. Rule:

- **Dependency wajib**: gunakan library resmi `com.google.android.material:material` versi terbaru (Material Components for Android) — JANGAN pakai `AppCompat` widget lama (`android.widget.Button`, `Toolbar` AppCompat polos) untuk komponen yang sudah punya versi M3 (`MaterialButton`, `MaterialToolbar`, `MaterialCardView`, dll).

- **Dynamic Color**: Wajib support dynamic color (Material You) di Android 12+ menggunakan `DynamicColors`:
```kotlin
// Di Application class, dipanggil di onCreate()
override fun onCreate() {
    super.onCreate()
    DynamicColors.applyToActivitiesIfAvailable(this)
}
```
Untuk fallback di Android < 12 atau saat dynamic color tidak tersedia, definisikan seed color palette manual di `colors.xml` / `themes.xml` (`values/themes.xml` dan `values-night/themes.xml` terpisah untuk light/dark).

- **Typography**: Definisikan skala teks lengkap lewat `TextAppearance` custom di `themes.xml` (turunan dari `TextAppearance.Material3.*`), lalu terapkan lewat `android:textAppearance` di setiap `TextView`/`MaterialTextView` — JANGAN hardcode `android:textSize` acak di setiap layout XML.
```xml
<style name="TextAppearance.App.TitleLarge" parent="TextAppearance.Material3.TitleLarge">
    <item name="fontFamily">@font/your_font_medium</item>
</style>
```

- **Shape System**: Gunakan `ShapeAppearance` custom (turunan `ShapeAppearance.Material3.*`) dengan radius konsisten (small/medium/large/extraLarge) di `themes.xml`, diterapkan via `app:shapeAppearance` pada `MaterialCardView`/`MaterialButton` — JANGAN hardcode `app:cornerRadius="8dp"` acak di banyak file XML berbeda.
```xml
<style name="ShapeAppearance.App.Medium" parent="ShapeAppearance.Material3.Corner.Medium">
    <item name="cornerSize">16dp</item>
</style>
```

- **Elevation & Tonal Surface**: Gunakan `app:cardElevation` dan `app:cardBackgroundColor` mengikuti token M3 (`?attr/colorSurfaceContainer`, dll) alih-alih shadow manual custom drawable.

- **Motion**: Transisi antar layar wajib pakai Android Transition Framework / Navigation Component (`Fragment` transitions dengan `MaterialSharedAxis`, `MaterialFadeThrough`, atau `MaterialContainerTransform` dari library `material`) — JANGAN pakai default `FragmentTransaction` tanpa animasi custom (default fade tidak cukup untuk produk "non-amatir").
```kotlin
exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
```

- **Komponen**: Prioritaskan komponen M3 resmi dari library `material` (`MaterialCardView`, `MaterialButton`, `TabLayout` dengan style M3, `BottomNavigationView`, `CollapsingToolbarLayout` untuk large top bar dengan collapsing behavior saat scroll) dibanding bikin custom `View`/drawable dari nol jika versi M3 sudah cukup.

---

## 4. Prinsip Desain yang Baik & Hierarki Visual

Bagian ini menjawab kenapa output Android sering terasa "amatir" walau secara fungsional jalan — biasanya karena agent tidak punya *mental model* desain yang benar, hanya menempel komponen tanpa hierarki yang jelas.

### 4.1 Hierarki Visual (Visual Hierarchy)

Setiap layar wajib punya **satu fokus utama** yang jelas, lalu elemen lain diturunkan levelnya secara sengaja. Cara membangun hierarki:

1. **Ukuran & Bobot Tipografi** — elemen paling penting (judul, angka utama, CTA) memakai `TextAppearance.Material3.HeadlineMedium`/`TitleLarge` (`android:textStyle="bold"` atau font weight medium/semibold lewat `fontFamily`). Elemen pendukung (label, deskripsi, metadata) memakai `TextAppearance.Material3.BodyMedium`/`LabelSmall` dengan `android:textColor="?attr/colorOnSurfaceVariant"` (lebih redup, bukan hitam pekat).
2. **Kontras Warna** — warna paling jenuh/kontras (`?attr/colorPrimary`, `?attr/colorOnPrimaryContainer`) hanya dipakai untuk elemen aksi/penting. Background dan elemen pasif menggunakan warna netral (`?attr/colorSurface`, `?attr/colorSurfaceVariant`).
3. **Spacing sebagai Pemisah, Bukan Dekorasi** — jarak yang lebih besar = pemisah grup yang berbeda secara konseptual. Jarak yang kecil = elemen yang berkaitan erat (misalnya icon dan label di sebelahnya). Jangan beri `layout_margin` yang sama rata ke semua elemen — itu menghilangkan hierarki.
4. **Posisi & Urutan Baca (F-Pattern / Z-Pattern)** — elemen yang dibaca pertama (judul, status, aksi utama) ditempatkan di area yang secara natural dilihat lebih dulu (atas, atau kiri-atas untuk LTR).
5. **Whitespace adalah Fitur, Bukan Ruang Kosong yang Harus Diisi** — JANGAN memaksa mengisi setiap ruang kosong dengan elemen tambahan. Negative space membantu mata fokus ke elemen penting.

### 4.2 Sistem Spacing & Grid yang Konsisten

Gunakan skala spasial 4dp/8dp sebagai dasar — JANGAN pakai angka acak (`6dp`, `10dp`, `14dp`, dll). Definisikan sebagai token di `res/values/dimens.xml`, bukan hardcode di setiap layout:

```xml
<!-- res/values/dimens.xml -->
<resources>
    <dimen name="spacing_xs">4dp</dimen>   <!-- jarak antar elemen yang sangat berkaitan (icon-text) -->
    <dimen name="spacing_sm">8dp</dimen>   <!-- jarak antar item dalam satu grup -->
    <dimen name="spacing_md">16dp</dimen>  <!-- padding standar konten / antar card -->
    <dimen name="spacing_lg">24dp</dimen>  <!-- jarak antar section -->
    <dimen name="spacing_xl">32dp</dimen>  <!-- jarak antar blok besar / margin atas-bawah layar -->
</resources>
```

Lalu dipakai secara konsisten di layout:
```xml
<TextView
    android:layout_marginTop="@dimen/spacing_lg"
    android:padding="@dimen/spacing_md" />
```

- Margin layar (kiri-kanan) konsisten: `16dp` untuk mobile standar, bisa `24dp` untuk layar besar (tablet/foldable — gunakan resource qualifier `values-sw600dp/dimens.xml` untuk override).
- Card/list item: padding internal konsisten (`16dp`), jarak antar card (`8dp`–`12dp`), diatur lewat `itemDecoration`/margin item di `RecyclerView`, bukan padding acak per-item.

### 4.3 Komposisi Layout per Jenis Layar

| Jenis Layar | Pola Layout yang Disarankan |
|---|---|
| Dashboard/Home | Hero/summary section di atas (info paling penting), lalu grid/list section di bawah, gunakan `RecyclerView` (`ConcatAdapter` untuk gabung beberapa section) dengan header section jelas |
| List/Feed | `RecyclerView` dengan `ListAdapter` + `DiffUtil`, item card konsisten (`MaterialCardView`), swipe action via `ItemTouchHelper` jika relevan — jangan campur jenis view type berbeda dalam satu list tanpa alasan visual |
| Detail/Form | Hierarki: judul besar → info utama → aksi sekunder → metadata kecil di paling bawah, gunakan `ScrollView`/`NestedScrollView` + `ConstraintLayout` |
| Settings | `PreferenceFragmentCompat` atau `RecyclerView` custom dengan header section (`ListItem` style + `MaterialDivider` antar grup, bukan satu list panjang tanpa pemisah) |
| Empty State | Ilustrasi/icon besar + `TextView` penjelas singkat + CTA `MaterialButton` jika relevan, ditampilkan lewat `ViewFlipper`/toggle visibility (jangan biarkan layar putih kosong) |

### 4.4 Haptic Feedback & Vibration API — Pola yang Benar

Vibrasi/haptic adalah **bahasa fisik** aplikasi — dipakai untuk menegaskan hasil aksi, bukan dekorasi yang dipasang sembarangan di semua tempat (over-haptic terasa murahan, sama amatirnya dengan tidak ada haptic sama sekali).

**Prioritaskan `View.performHapticFeedback()`** untuk interaksi UI standar (ini sudah otomatis menyesuaikan intensitas sesuai jenis interaksi & device):

```kotlin
binding.btnDelete.setOnClickListener { view ->
    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM) // gunakan untuk konfirmasi aksi penting
    onConfirmDelete()
}
```

Mapping `HapticFeedbackConstants` yang benar sesuai konteks:

| Konteks Interaksi | Jenis Feedback |
|---|---|
| Tap tombol biasa | Tidak perlu haptic eksplisit (cukup ripple visual M3 dari `?attr/selectableItemBackground`) |
| Konfirmasi aksi penting (delete, submit, toggle critical setting) | `HapticFeedbackConstants.CONFIRM` (API 30+) atau `LONG_PRESS` sebagai fallback (terasa lebih "berat"/tegas) |
| Drag mulai / item terangkat (reorder list via `ItemTouchHelper`) | `HapticFeedbackConstants.DRAG_START` (API 34+) atau `LONG_PRESS` untuk API di bawahnya |
| Snap ke posisi (`Slider`, `SeekBar` mencapai nilai bulat) | `HapticFeedbackConstants.SEGMENT_TICK` / `CLOCK_TICK` (tick halus) |
| Reject/error (input tidak valid) | `HapticFeedbackConstants.REJECT` (API 30+) |

**Gunakan `Vibrator`/`VibratorManager` API langsung** hanya untuk kasus yang butuh kontrol pola custom di luar kemampuan `performHapticFeedback` (misalnya notifikasi penting, alarm, game feedback):

```kotlin
val vibratorManager = context.getSystemService(VibratorManager::class.java)
val vibrator = vibratorManager.defaultVibrator

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    vibrator.vibrate(
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
    )
} else {
    @Suppress("DEPRECATION")
    vibrator.vibrate(50) // fallback durasi pendek untuk API lama
}
```

- ❌ **DILARANG** memanggil `vibrate()` mentah dengan durasi panjang sembarangan (`vibrate(500)`) untuk interaksi UI ringan — terasa kasar dan tidak natural.
- ❌ **DILARANG** memberi haptic di SETIAP tap (termasuk navigasi biasa, scroll, switch tab) — ini menyebabkan *haptic fatigue*, justru bikin app terasa murahan.
- ✅ Haptic hanya untuk: konfirmasi aksi destruktif/penting, hasil aksi (sukses/gagal), milestone interaksi (drag-drop, snap, long-press menu), bukan untuk navigasi pasif.
- ✅ Selalu cek permission `VIBRATE` di manifest jika menggunakan `Vibrator` API langsung (umumnya normal permission, tapi tetap wajib dideklarasikan):
```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

### 4.5 Feedback System Secara Umum (Visual, Audio, Haptic)

Setiap aksi user wajib punya **minimal satu bentuk feedback** yang sesuai bobot aksinya:

| Bobot Aksi | Feedback yang Sesuai |
|---|---|
| Ringan (tap, toggle biasa) | Ripple/state visual M3 saja sudah cukup |
| Sedang (submit form, tambah item, like) | Visual (animasi micro-interaction, perubahan state ikon) + opsional haptic ringan |
| Penting/Destruktif (hapus, logout, reset) | Dialog konfirmasi (`MaterialAlertDialogBuilder`) + haptic `CONFIRM`/`LONG_PRESS` + `Snackbar` setelah aksi selesai ("Item dihapus" + tombol Undo jika relevan) |
| Async/Network (loading, sync) | Progress indicator yang jelas statusnya (loading → success/error), JANGAN biarkan UI freeze tanpa indikasi |

---



## 5. Pemanfaatan API Android Modern (Bukan Cuma "Jalan")

Agent sering hanya membuat fitur "berjalan" tanpa memanfaatkan API platform yang relevan. Rule wajib cek per kategori fitur:

| Kategori Fitur | API yang Wajib Dipertimbangkan |
|---|---|
| Notifikasi | `NotificationChannel`, `POST_NOTIFICATIONS` runtime permission (API 33+) |
| Background task | `WorkManager` (bukan `Service` mentah / `Thread` manual) |
| File & Media akses | `Photo Picker` (`ActivityResultContracts.PickVisualMedia`) — bukan minta `READ_EXTERNAL_STORAGE` penuh jika tidak perlu |
| Permission | `Runtime Permission` granular sesuai API level, gunakan `ActivityResultContracts.RequestPermission` / `RequestMultiplePermissions` native, dicek lewat `registerForActivityResult` di `Activity`/`Fragment` |
| Sharing | `ShareSheet` via `Intent.ACTION_SEND` + `FileProvider`, bukan custom share UI |
| Predictive Back | Wajib support **Predictive Back Gesture** (Android 13+) — daftarkan `OnBackPressedCallback` lewat `OnBackPressedDispatcher`, jangan override `onBackPressed()` lama tanpa dispatcher |
| Splash Screen | `SplashScreen API` (`core-splashscreen`), dipasang di `themes.xml` (`Theme.SplashScreen`), bukan `Activity` placeholder manual |
| Haptic | `View.performHapticFeedback()` / `Vibrator` API — lihat Section 4.4 untuk detail lengkap pola getar yang benar |
| Battery/Doze | Hindari `WakeLock` jika tugas bisa pakai `WorkManager` constraint-based |
| Tema sistem | Wajib respect `Force Dark` / dynamic theme lewat `values-night/` resource qualifier, jangan hardcode warna yang menabrak system theme |

---

## 6. Struktur Project (Wajib Diikuti Agent)

```
app/
 └─ src/main/
     ├─ java/<package>/
     │   ├─ data/
     │   │   ├─ local/        (Room, DataStore)
     │   │   ├─ remote/        (jika ada network)
     │   │   └─ repository/
     │   ├─ domain/
     │   │   ├─ model/
     │   │   └─ usecase/
     │   ├─ presentation/
     │   │   ├─ <feature>/
     │   │   │   ├─ <Feature>Activity.kt / <Feature>Fragment.kt
     │   │   │   ├─ <Feature>ViewModel.kt
     │   │   │   ├─ <Feature>UiState.kt
     │   │   │   └─ <Feature>Adapter.kt   (jika ada RecyclerView)
     │   │   └─ common/        (BaseActivity/BaseFragment, shared UI helper)
     │   └─ di/                 (Hilt modules)
     └─ res/
         ├─ layout/             (XML layout per screen/item)
         ├─ values/             (colors.xml, dimens.xml, strings.xml, themes.xml, styles.xml)
         ├─ values-night/       (override warna/tema untuk dark mode)
         └─ drawable/           (vector drawable, shape drawable, selector)
```

- Setiap `ViewModel` wajib expose `StateFlow<UiState>` (dikoleksi lewat `lifecycleScope.launch { repeatOnLifecycle(...) }` di `Activity`/`Fragment`) — bukan callback lepas tanpa state class.
- Setiap layar wajib punya state: `Loading`, `Success`, `Empty`, `Error` — direpresentasikan lewat toggle visibility antar view (`ProgressBar`, konten utama, empty state layout, error layout) menggunakan `ViewFlipper` atau `View.GONE`/`VISIBLE` terkontrol dari satu fungsi `render(state)` — jangan biarkan UI "diam" tanpa feedback visual saat proses berjalan.

> ⚠️ **Batasan penting (sering disalahartikan agent):** toggle `View.GONE`/`VISIBLE`/`ViewFlipper` di atas **HANYA** berlaku untuk mengganti *state* di dalam **satu layar/Fragment yang sama** (loading ↔ isi ↔ kosong ↔ error). Ini **BUKAN** mekanisme untuk berpindah antar "halaman"/fitur yang berbeda (mis. Home → Profile, atau Home → Detail Item). Perpindahan antar layar/fitur WAJIB lewat **Navigation Component** (`Fragment` + `nav_graph.xml`) sesuai Section 6.1 di bawah. Kalau agent mendapati dirinya mau menyembunyikan satu `ConstraintLayout` besar dan memunculkan `ConstraintLayout` besar lain di root yang sama untuk mensimulasikan "pindah halaman", itu tanda dia salah menerapkan rule ini — harusnya itu jadi Fragment terpisah.

- Gunakan **View Binding** (`viewBinding = true` di `build.gradle.kts`), JANGAN `findViewById` manual atau Kotlin Android Extensions (`synthetic`, sudah deprecated).

### 6.1 Navigasi Multi-Layar (Wajib — Single-Activity + Navigation Component)

Aplikasi ini menggunakan **Single-Activity Architecture**. Satu `MainActivity` menjadi host untuk seluruh `Fragment` di aplikasi, dikelola oleh **Navigation Component** (`androidx.navigation`). Ini bukan opsi — ini adalah struktur wajib untuk semua fitur yang butuh lebih dari satu "layar".

**Urutan pembuatan file wajib** (jangan dibalik):
1. `res/menu/bottom_nav_menu.xml` (atau menu tab lain) — id item harus identik dengan id Fragment tujuan di nav_graph.
2. `res/navigation/nav_graph.xml` — daftarkan semua destination (Fragment), startDestination, dan `<action>` (dengan `<argument>`/Safe Args kalau ada data yang dikirim).
3. Fragment `.kt` + layout `.xml` masing-masing.
4. `activity_main.xml` — `FragmentContainerView` (`app:navGraph`) + `BottomNavigationView`/`TabLayout`.
5. `MainActivity.kt` — wiring `NavController` ke nav bar via `setupWithNavController()`.

**Aturan tambahan:**
- Setiap `Fragment` baru **wajib** terdaftar di `nav_graph.xml` sebelum dianggap selesai — Fragment yang tidak terhubung ke graph adalah bug.
- Transisi antar destination wajib pakai `enterAnim`/`exitAnim`/`popEnterAnim`/`popExitAnim` di `<action>`, atau `MaterialSharedAxis`/`MaterialFadeThrough`/`MaterialContainerTransform` (lihat Section 3) — jangan biarkan transisi default tanpa animasi.
- Untuk sub-navigasi di dalam satu section (mis. tab "Semua/Lagu/Album" di dalam satu Fragment), gunakan `TabLayout` + `ViewPager2` + `TabLayoutMediator` — ini valid dan berbeda dari top-level navigation, lihat `AGENTS.md` untuk contoh kode lengkap kedua pattern ini (Bottom Nav & Top Tabs) serta pattern nested graph untuk flow multi-step (onboarding, auth, checkout).
- **DILARANG**: Activity terpisah per fitur/tab (`HomeActivity`, `SearchActivity`, dst) kecuali untuk kasus yang benar-benar butuh Task/back-stack terpisah (mis. full-screen player independen) — dan itu pun harus didiskusikan eksplisit, bukan default.
- **DILARANG**: `FragmentTransaction.replace()` manual di luar Navigation Component untuk perpindahan antar fitur.

Detail contoh kode lengkap (nav_graph.xml, bottom_nav_menu.xml, activity_main.xml, MainActivity.kt, ViewPager2 pattern, nested graph, tabel anti-pattern) ada di `AGENTS.md` Section 1 — dokumen itu melengkapi section ini dengan implementasi penuh siap pakai.

---

## 7. GitHub Actions — Build & Sign APK (Strict Flow)

### 7.1 Trigger
- Workflow dijalankan otomatis **setiap `push` ke branch `main`**, plus tetap bisa dipicu manual (`workflow_dispatch`) kapan saja.
- **Dilarang** auto-deploy ke Play Store / server lain. Output **hanya** GitHub Artifacts.
- Tidak pakai trigger berbasis tag (`v*.*.*`) — karena ini project personal, setiap push ke `main` dianggap siap jadi build APK terbaru.

### 7.2 ⚠️ WAJIB: Generate & Verifikasi Keystore Dummy SEBELUM Build Dimulai

> **Catatan konteks**: Ini project personal (bukan untuk rilis ke Play Store), jadi keystore **tidak perlu** disimpan sebagai GitHub Secret atau file eksternal. Keystore self-signed dummy digenerate otomatis di dalam job lewat `keytool -genkeypair`, dengan password/alias hardcode langsung di workflow. Ini jauh lebih simpel untuk keperluan personal — tidak ada secret yang perlu di-setup atau bisa hilang/expire.
>
> ⚠️ Karena keystore digenerate ulang setiap run (bukan disimpan persisten), APK dari build berbeda **tidak akan bisa saling update/replace** di device yang sama (signature beda tiap kali). Untuk keperluan personal (build → download APK dari Artifacts → install manual), ini tidak masalah. Kalau suatu saat butuh keystore yang konsisten antar build (misal untuk auto-update APK di device), keystore dummy ini bisa di-cache/commit sebagai file di repo (bukan lagi digenerate ulang tiap kali) — tapi itu di luar cakupan default project ini.

Step wajib dilakukan secara berurutan:
1. **Generate keystore dummy** dengan `keytool -genkeypair` (self-signed, validitas panjang, password/alias hardcode).
2. **Validasi keystore hasil generate** dengan `keytool -list` — memastikan file benar-benar valid sebelum dipakai build (fail-fast kalau ada error tak terduga).
3. **Build & sign APK** memakai keystore dummy tersebut.
4. **Verifikasi APK ter-sign** dengan `apksigner verify`.

```yaml
name: Build Signed APK

on:
  push:
    branches:
      - main
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    env:
      # Dummy keystore config — HARDCODE, bukan secret, karena project personal & keystore self-signed sementara.
      KEYSTORE_PASSWORD: dummyPassword123
      KEY_ALIAS: dummyalias
      KEY_PASSWORD: dummyPassword123
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: gradle

      # === STEP WAJIB: Generate keystore dummy SEBELUM build ===
      - name: Generate Dummy Debug-style Keystore
        run: |
          keytool -genkeypair \
            -v \
            -keystore app/release.keystore \
            -alias "$KEY_ALIAS" \
            -keyalg RSA \
            -keysize 2048 \
            -validity 10000 \
            -storepass "$KEYSTORE_PASSWORD" \
            -keypass "$KEY_PASSWORD" \
            -dname "CN=Personal Dev, OU=Personal, O=Personal, L=Unknown, S=Unknown, C=ID"
          echo "✅ Dummy keystore berhasil digenerate."

      - name: Validate Generated Keystore
        run: |
          if [ ! -s app/release.keystore ]; then
            echo "::error::File keystore gagal dibuat."
            exit 1
          fi

          keytool -list -v \
            -keystore app/release.keystore \
            -storepass "$KEYSTORE_PASSWORD" \
            -alias "$KEY_ALIAS" > keystore_check.log 2>&1

          if [ $? -ne 0 ]; then
            echo "::error::Keystore gagal di-inisialisasi setelah digenerate. Cek log."
            cat keystore_check.log
            exit 1
          fi

          echo "✅ Keystore valid dan berhasil di-inisialisasi dengan alias '$KEY_ALIAS'."

      - name: Build Release APK
        run: ./gradlew assembleRelease

      - name: Verify APK is Actually Signed
        run: |
          APK_PATH=$(find app/build/outputs/apk/release -name "*.apk" | head -n 1)
          if [ -z "$APK_PATH" ]; then
            echo "::error::Tidak ada APK ditemukan setelah build."
            exit 1
          fi
          $ANDROID_HOME/build-tools/*/apksigner verify --verbose "$APK_PATH"
          if [ $? -ne 0 ]; then
            echo "::error::APK tidak lolos verifikasi signature (apksigner)."
            exit 1
          fi
          echo "✅ APK terkonfirmasi sudah ter-sign dengan benar."

      - name: Upload Signed APK
        uses: actions/upload-artifact@v4
        with:
          name: app-release-signed
          path: app/build/outputs/apk/release/*.apk
```

> **Catatan penting untuk agent**: Jangan pernah langsung lompat ke `assembleRelease` tanpa step generate + validasi keystore di atas. Urutan wajib: **(1) generate keystore dummy → (2) validasi keystore dengan `keytool` → (3) baru build → (4) verifikasi hasil APK dengan `apksigner`.** Tidak ada step "cek GitHub Secrets" lagi karena keystore tidak lagi bergantung pada secrets eksternal.

### 7.4 Aturan `build.gradle.kts` untuk Signing
```kotlin
android {
    signingConfigs {
        create("release") {
            // Nilai ini datang dari env var yang di-set workflow (lihat Section 7.2) —
            // untuk project personal ini env var berisi password dummy hardcode,
            // BUKAN dari GitHub Secrets. Jangan tambahkan step "cek secrets" lagi.
            storeFile = file("release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### 7.5 Checklist Rilis
- [ ] Step generate + validasi keystore dummy (Section 7.2) lolos tanpa error.
- [ ] APK hasil build adalah `release`, bukan `debug`.
- [ ] APK sudah ter-sign (cek dengan `apksigner verify`).
- [ ] `versionCode` & `versionName` naik otomatis setiap push ke `main` (mis. pakai `github.run_number` sebagai `versionCode`, atau counter file di repo) — tidak lagi berbasis tag git karena trigger sekarang per-push.
- [ ] Artifact diberi nama jelas, mis. `app-release-run${{ github.run_number }}-signed.apk`.
- [ ] Tidak ada password keystore dummy yang sensitif (karena ini hanya dummy self-signed personal, risiko expose rendah — tapi tetap jangan print isi keystore mentah ke log).

---

## 8. Definisi "Selesai" (Definition of Done)

Sebuah task TIDAK dianggap selesai jika hanya "compile berhasil". Agent wajib pastikan:

1. ✅ App build tanpa warning kritikal dari lint Android.
2. ✅ Edge-to-edge & status bar sesuai Section 2 (visually verified secara konsep, bukan asumsi).
3. ✅ Tidak ada hardcoded string UI (gunakan `strings.xml` / resource, walau app single-language) dan tidak ada hardcoded dimensi (gunakan `dimens.xml`, lihat Section 4.2).
4. ✅ Tidak ada `TODO` kosong atau fitur stub yang dibiarkan tanpa state handling.
5. ✅ Setiap layout XML kompleks sudah dicek dengan **Layout Inspector** / preview `tools:` namespace di Android Studio (`tools:text`, `tools:visibility`) untuk mempermudah review visual tanpa perlu run aplikasi penuh.
6. ✅ Tidak ada `View Binding` yang di-null-kan secara salah (khususnya di `Fragment` — wajib set `_binding = null` di `onDestroyView()` untuk mencegah memory leak).
7. ✅ APK ter-build dan ter-sign lewat GitHub Actions, muncul di tab Artifacts.

---

## 9. Larangan Umum (Red Flags "Amatir")

- ❌ Menggunakan `Toast` sebagai satu-satunya bentuk feedback untuk aksi penting (gunakan `Snackbar` dari library `material` atau `MaterialAlertDialogBuilder` yang sesuai).
- ❌ Loading state hanya `ProgressBar` polos di tengah layar tanpa skeleton/shimmer (`ShimmerFrameLayout` atau custom shimmer drawable) untuk list/grid.
- ❌ Spacing tidak konsisten (campur `8dp`, `10dp`, `12dp` acak langsung di XML) — wajib pakai token `@dimen/spacing_*` dari Section 4.2.
- ❌ Tidak ada empty state ilustratif (list kosong hanya menampilkan layar putih kosong — wajib toggle ke layout empty state).
- ❌ Tombol/icon tidak punya `android:contentDescription` (accessibility diabaikan) — untuk elemen dekoratif murni, set eksplisit `android:importantForAccessibility="no"`.
- ❌ Tidak ada haptic/animasi feedback sama sekali pada interaksi penting.
- ❌ Menggunakan `findViewById` manual atau Kotlin synthetic import padahal View Binding sudah wajib diaktifkan.
- ❌ `RecyclerView` tanpa `DiffUtil`/`ListAdapter` (`notifyDataSetChanged()` mentah tanpa animasi item, terasa kasar dan tidak efisien).

---

## 10. Instruksi Akhir untuk Agent

> Sebelum menulis kode, jelaskan dulu secara singkat rencana implementasi UI/UX dan bagaimana Section 2 (Edge-to-Edge), Section 4 (Desain & Hierarki Visual), dan Section 6.1 (Navigasi) akan diterapkan di layar tersebut. Setelah itu, tulis kode lengkap (layout XML + Kotlin). Jangan submit kode yang melanggar satupun rule strict di atas (Section 2, 4, 6.1, 7, dan 8 adalah yang paling sering dilanggar — periksa dua kali). Untuk implementasi navigasi, SVG/icon detail, dan pola desain lanjutan lainnya, rujuk `AGENTS.md` sebagai referensi teknis lengkap.
