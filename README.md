# 🩸 BloodConnect (Android — Kotlin + Jetpack Compose)

Aplikasi mobile **native Android** untuk mencari & menghubungkan **donor darah** berdasarkan **golongan darah** dan **kedekatan wilayah** (Provinsi → Kota/Kabupaten → Kecamatan). Dibuat untuk MK Pemrograman Berbasis Mobile, mendukung **SDG 3 — Good Health and Well-being**.

Arsitektur **MVVM** (UI Compose → `AppViewModel` → `Repository`). Tema mengikuti arah desain `DESIGN.md` (aksen tunggal Rausch `#ff385c`, kanvas putih, sudut membulat).

---

## ⚠️ PENTING — Cara mendapatkan file APK

Project ini **belum berisi file `.apk` jadi** dan **belum berisi `gradle-wrapper.jar`** (file biner). APK harus kamu **build sendiri** — ini cepat dan mudah:

### Cara A — Android Studio (paling direkomendasikan)
1. Install **Android Studio** (gratis) — sudah termasuk Android SDK & Gradle.
2. **Open** → pilih folder `BloodConnectAndroid`.
3. Tunggu **Gradle Sync** selesai (Android Studio otomatis mengunduh Gradle + SDK yang diperlukan dan membuat `gradle-wrapper.jar`).
4. Menu **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. Setelah selesai, klik **locate** — file ada di:
   `app/build/outputs/apk/debug/app-debug.apk`
6. Kirim/instal `app-debug.apk` itu ke HP Android (aktifkan “Install from unknown sources”).

### Cara B — Command line (kalau sudah punya Android SDK + Gradle)
```bash
cd BloodConnectAndroid
gradle wrapper            # sekali saja, untuk membuat gradle-wrapper.jar
./gradlew assembleDebug   # Windows: gradlew.bat assembleDebug
```
Hasil: `app/build/outputs/apk/debug/app-debug.apk`

> Untuk APK rilis yang bisa dibagikan luas, gunakan `./gradlew assembleRelease` lalu tandatangani (signing) lewat **Build → Generate Signed Bundle / APK** di Android Studio.

---

## ✨ Fitur

- **Beranda**: hero, tombol aksi, daftar pendonor tersedia, dan “Cara kerja”.
- **Registrasi & Login** donor (data disimpan in-memory untuk demo).
- **Cari Donor**: filter golongan darah (otomatis menampilkan golongan **kompatibel** untuk penerima) + wilayah bertingkat, hasil **diurutkan dari yang terdekat** (kecamatan → kota → provinsi), tanpa GPS.
- **Detail Donor**: kontak hanya muncul setelah “Minta Kontak Donor” (privasi) + tombol **WhatsApp** & **Telepon**.
- **Dashboard**: toggle status ketersediaan, catat tanggal donor terakhir, hitung **masa jeda kelayakan** (75 hari), edit profil.

## 📱 Spesifikasi Teknis

- **Bahasa**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Arsitektur**: MVVM (`ViewModel` + `StateFlow`)
- **Navigasi**: Navigation Compose
- **minSdk** 26, **targetSdk/compileSdk** 34
- **Android Gradle Plugin** 8.5.2, **Kotlin** 1.9.24, **Gradle** 8.7

## 📁 Struktur

```
BloodConnectAndroid/
├─ settings.gradle.kts / build.gradle.kts / gradle.properties
├─ gradle/wrapper/gradle-wrapper.properties
└─ app/
   ├─ build.gradle.kts
   └─ src/main/
      ├─ AndroidManifest.xml
      ├─ res/ (icon adaptif, warna, tema, string)
      └─ java/com/bloodconnect/app/
         ├─ MainActivity.kt          # NavHost + TopBar
         ├─ data/
         │  ├─ Models.kt             # Donor, Location, DonorMatch
         │  ├─ Regions.kt            # Wilayah + golongan darah + kompatibilitas
         │  └─ Repository.kt         # Logika: auth, eligibility, search, ranking
         └─ ui/
            ├─ AppViewModel.kt       # MVVM ViewModel
            ├─ Components.kt         # DonorCard, Dropdown, dll.
            ├─ theme/Theme.kt        # Design tokens
            └─ screens/              # Home, Search, DonorDetail, Register, Login, Dashboard
```

## ☁️ WAJIB: Setup Firebase (agar data nyambung antar HP)

Aplikasi ini memakai **Firebase Authentication + Cloud Firestore** untuk sinkronisasi real-time antar perangkat. Lakukan langkah ini **SEKALI** sebelum build (gratis):

1. Buka https://console.firebase.google.com → **Add project** → beri nama (mis. *BloodConnect*) → lanjut sampai selesai (Google Analytics boleh dimatikan).
2. Di dalam project, klik ikon **Android** untuk menambahkan app:
   - **Android package name:** `com.bloodconnect.app` (WAJIB sama persis)
   - Nickname & SHA-1 boleh dikosongkan → **Register app**.
3. **Download `google-services.json`**, lalu letakkan di folder **`app/`** (sejajar dengan `app/build.gradle.kts`). File ini wajib ada — tanpa ini build akan gagal.
4. Menu kiri **Build → Authentication → Get started → Sign-in method → Email/Password → Enable**.
5. Menu kiri **Build → Firestore Database → Create database → Start in test mode** → pilih lokasi (mis. `asia-southeast1`) → Enable.
   - *Test mode* memudahkan demo. Untuk produksi, atur Security Rules.
6. Kembali ke Android Studio → **Sync Project with Gradle Files** → lalu **Run ▶** / Build APK.

> ⚠️ Tanpa langkah 3, Gradle error: *"File google-services.json is missing"*.

## 👤 Data Contoh & Cara Uji Sinkronisasi

Saat pertama dijalankan (Firestore masih kosong), aplikasi otomatis mengisi **10 donor contoh** (mayoritas Sumatera Barat) agar pencarian langsung ada isinya. Donor contoh ini **bisa dicari** tetapi tidak punya akun login.

**Uji antar 2 HP:** di **HP A** daftar akun donor baru (email + password, min. 6 karakter). Buka **HP B**, lalu cari/lihat — donor dari HP A langsung muncul. Ubah status/profil di HP A, perubahannya ikut **real-time** di HP B. 🔄

> Kedua HP harus **online** (ada internet).

## 🔄 Arsitektur data

Semua akses Firebase terpusat di `data/Repository.kt` (Auth + Firestore + listener real-time), terpisah dari UI (MVVM). Koleksi Firestore: **`donors`** — satu dokumen per donor dengan ID = UID akun.

## ⚠️ Disclaimer

Aplikasi demo edukatif. Penyimpanan password apa adanya hanya untuk tugas, **bukan untuk produksi**. Donor darah sesungguhnya harus melalui **PMI / fasilitas kesehatan resmi**.
