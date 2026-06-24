package com.bloodconnect.app.data

/** Static reference data: blood types, donor->recipient compatibility, and the
 *  administrative region hierarchy (Provinsi -> Kota/Kabupaten -> Kecamatan).
 *  Mencakup seluruh 38 provinsi Indonesia (dengan contoh kota/kabupaten & kecamatan). */
object Regions {

    val BLOOD_TYPES = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    /** For a given recipient blood type, the list of donor blood types that can donate to them. */
    val COMPATIBILITY: Map<String, List<String>> = mapOf(
        "A+" to listOf("A+", "A-", "O+", "O-"),
        "A-" to listOf("A-", "O-"),
        "B+" to listOf("B+", "B-", "O+", "O-"),
        "B-" to listOf("B-", "O-"),
        "AB+" to listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"),
        "AB-" to listOf("A-", "B-", "AB-", "O-"),
        "O+" to listOf("O+", "O-"),
        "O-" to listOf("O-")
    )

    /** province -> (city -> list of districts). Seluruh 38 provinsi Indonesia. */
    val DATA: Map<String, Map<String, List<String>>> = mapOf(
        "Aceh" to mapOf(
            "Banda Aceh" to listOf("Baiturrahman", "Kuta Alam", "Syiah Kuala", "Meuraxa", "Banda Raya", "Ulee Kareng"),
            "Lhokseumawe" to listOf("Banda Sakti", "Muara Dua", "Muara Satu", "Blang Mangat"),
            "Langsa" to listOf("Langsa Kota", "Langsa Barat", "Langsa Timur", "Langsa Lama"),
            "Aceh Besar" to listOf("Ingin Jaya", "Darul Imarah", "Kuta Baro", "Lhoknga")
        ),
        "Sumatera Utara" to mapOf(
            "Medan" to listOf("Medan Baru", "Medan Kota", "Medan Tembung", "Medan Johor"),
            "Binjai" to listOf("Binjai Kota", "Binjai Utara", "Binjai Selatan"),
            "Pematangsiantar" to listOf("Siantar Barat", "Siantar Timur", "Siantar Utara")
        ),
        "Sumatera Barat" to mapOf(
            "Padang" to listOf("Padang Barat", "Padang Timur", "Padang Utara", "Kuranji", "Lubuk Begalung"),
            "Bukittinggi" to listOf("Guguk Panjang", "Mandiangin Koto Selayan", "Aur Birugo Tigo Baleh"),
            "Padang Panjang" to listOf("Padang Panjang Barat", "Padang Panjang Timur")
        ),
        "Riau" to mapOf(
            "Pekanbaru" to listOf("Sukajadi", "Senapelan", "Marpoyan Damai", "Tampan"),
            "Dumai" to listOf("Dumai Barat", "Dumai Timur", "Bukit Kapur")
        ),
        "Jambi" to mapOf(
            "Jambi" to listOf("Telanaipura", "Jambi Selatan", "Jelutung", "Kota Baru"),
            "Sungai Penuh" to listOf("Sungai Penuh", "Pesisir Bukit", "Hamparan Rawang")
        ),
        "Sumatera Selatan" to mapOf(
            "Palembang" to listOf("Ilir Barat I", "Ilir Timur I", "Seberang Ulu I", "Sukarami"),
            "Prabumulih" to listOf("Prabumulih Barat", "Prabumulih Timur", "Cambai")
        ),
        "Bengkulu" to mapOf(
            "Bengkulu" to listOf("Teluk Segara", "Gading Cempaka", "Ratu Agung", "Muara Bangkahulu")
        ),
        "Lampung" to mapOf(
            "Bandar Lampung" to listOf("Tanjung Karang Pusat", "Kedaton", "Rajabasa", "Sukarame"),
            "Metro" to listOf("Metro Pusat", "Metro Timur", "Metro Barat")
        ),
        "Kepulauan Bangka Belitung" to mapOf(
            "Pangkalpinang" to listOf("Taman Sari", "Rangkui", "Bukit Intan", "Gerunggang"),
            "Belitung" to listOf("Tanjung Pandan", "Membalong", "Badau")
        ),
        "Kepulauan Riau" to mapOf(
            "Batam" to listOf("Batam Kota", "Lubuk Baja", "Sekupang", "Batu Ampar"),
            "Tanjungpinang" to listOf("Tanjungpinang Kota", "Bukit Bestari", "Tanjungpinang Timur")
        ),
        "DKI Jakarta" to mapOf(
            "Jakarta Pusat" to listOf("Menteng", "Tanah Abang", "Gambir", "Senen"),
            "Jakarta Selatan" to listOf("Kebayoran Baru", "Setiabudi", "Tebet", "Mampang Prapatan"),
            "Jakarta Barat" to listOf("Grogol Petamburan", "Kebon Jeruk", "Palmerah"),
            "Jakarta Timur" to listOf("Matraman", "Jatinegara", "Duren Sawit", "Cakung"),
            "Jakarta Utara" to listOf("Tanjung Priok", "Kelapa Gading", "Penjaringan")
        ),
        "Jawa Barat" to mapOf(
            "Bandung" to listOf("Coblong", "Sukajadi", "Bandung Wetan", "Cibeunying Kidul"),
            "Bekasi" to listOf("Bekasi Timur", "Bekasi Barat", "Bekasi Selatan"),
            "Bogor" to listOf("Bogor Tengah", "Bogor Utara", "Tanah Sareal"),
            "Depok" to listOf("Beji", "Pancoran Mas", "Cimanggis", "Sukmajaya")
        ),
        "Jawa Tengah" to mapOf(
            "Semarang" to listOf("Semarang Tengah", "Tembalang", "Banyumanik", "Candisari"),
            "Surakarta" to listOf("Laweyan", "Jebres", "Banjarsari", "Serengan"),
            "Magelang" to listOf("Magelang Tengah", "Magelang Utara", "Magelang Selatan")
        ),
        "DI Yogyakarta" to mapOf(
            "Kota Yogyakarta" to listOf("Gondokusuman", "Umbulharjo", "Mergangsan", "Jetis"),
            "Sleman" to listOf("Depok", "Mlati", "Ngaglik", "Gamping"),
            "Bantul" to listOf("Bantul", "Sewon", "Kasihan", "Banguntapan")
        ),
        "Jawa Timur" to mapOf(
            "Surabaya" to listOf("Gubeng", "Wonokromo", "Sukolilo", "Tegalsari"),
            "Malang" to listOf("Klojen", "Lowokwaru", "Blimbing", "Sukun"),
            "Sidoarjo" to listOf("Sidoarjo", "Waru", "Taman", "Krian")
        ),
        "Banten" to mapOf(
            "Serang" to listOf("Serang", "Cipocok Jaya", "Kasemen", "Walantaka"),
            "Tangerang" to listOf("Tangerang", "Cipondoh", "Ciledug", "Karawaci"),
            "Tangerang Selatan" to listOf("Ciputat", "Pamulang", "Serpong", "Pondok Aren")
        ),
        "Bali" to mapOf(
            "Denpasar" to listOf("Denpasar Barat", "Denpasar Selatan", "Denpasar Utara", "Denpasar Timur"),
            "Badung" to listOf("Kuta", "Mengwi", "Abiansemal", "Kuta Selatan"),
            "Gianyar" to listOf("Gianyar", "Ubud", "Sukawati", "Blahbatuh")
        ),
        "Nusa Tenggara Barat" to mapOf(
            "Mataram" to listOf("Ampenan", "Cakranegara", "Mataram", "Selaparang"),
            "Bima" to listOf("Rasanae Barat", "Rasanae Timur", "Mpunda", "Raba")
        ),
        "Nusa Tenggara Timur" to mapOf(
            "Kupang" to listOf("Kota Lama", "Oebobo", "Kelapa Lima", "Maulafa"),
            "Ende" to listOf("Ende Tengah", "Ende Selatan", "Ende Timur", "Ende Utara")
        ),
        "Kalimantan Barat" to mapOf(
            "Pontianak" to listOf("Pontianak Kota", "Pontianak Selatan", "Pontianak Timur", "Pontianak Barat"),
            "Singkawang" to listOf("Singkawang Tengah", "Singkawang Barat", "Singkawang Utara")
        ),
        "Kalimantan Tengah" to mapOf(
            "Palangka Raya" to listOf("Pahandut", "Jekan Raya", "Bukit Batu", "Sebangau")
        ),
        "Kalimantan Selatan" to mapOf(
            "Banjarmasin" to listOf("Banjarmasin Tengah", "Banjarmasin Utara", "Banjarmasin Selatan", "Banjarmasin Timur"),
            "Banjarbaru" to listOf("Banjarbaru Utara", "Banjarbaru Selatan", "Landasan Ulin", "Cempaka")
        ),
        "Kalimantan Timur" to mapOf(
            "Samarinda" to listOf("Samarinda Kota", "Samarinda Ulu", "Sungai Kunjang", "Samarinda Seberang"),
            "Balikpapan" to listOf("Balikpapan Kota", "Balikpapan Selatan", "Balikpapan Utara", "Balikpapan Timur")
        ),
        "Kalimantan Utara" to mapOf(
            "Tarakan" to listOf("Tarakan Tengah", "Tarakan Barat", "Tarakan Timur", "Tarakan Utara"),
            "Bulungan" to listOf("Tanjung Selor", "Tanjung Palas", "Sekatak")
        ),
        "Sulawesi Utara" to mapOf(
            "Manado" to listOf("Wenang", "Sario", "Malalayang", "Tikala"),
            "Bitung" to listOf("Bitung Tengah", "Bitung Utara", "Bitung Selatan")
        ),
        "Sulawesi Tengah" to mapOf(
            "Palu" to listOf("Palu Barat", "Palu Timur", "Palu Selatan", "Palu Utara")
        ),
        "Sulawesi Selatan" to mapOf(
            "Makassar" to listOf("Makassar", "Rappocini", "Tamalate", "Panakkukang"),
            "Parepare" to listOf("Bacukiki", "Ujung", "Soreang"),
            "Palopo" to listOf("Wara", "Wara Utara", "Telluwanua")
        ),
        "Sulawesi Tenggara" to mapOf(
            "Kendari" to listOf("Mandonga", "Kendari", "Poasia", "Kambu"),
            "Baubau" to listOf("Betoambari", "Murhum", "Wolio")
        ),
        "Gorontalo" to mapOf(
            "Gorontalo" to listOf("Kota Tengah", "Kota Selatan", "Kota Utara", "Dungingi")
        ),
        "Sulawesi Barat" to mapOf(
            "Mamuju" to listOf("Mamuju", "Simboro", "Kalukku", "Tapalang")
        ),
        "Maluku" to mapOf(
            "Ambon" to listOf("Sirimau", "Nusaniwe", "Teluk Ambon", "Baguala")
        ),
        "Maluku Utara" to mapOf(
            "Ternate" to listOf("Ternate Tengah", "Ternate Utara", "Ternate Selatan", "Pulau Ternate"),
            "Tidore Kepulauan" to listOf("Tidore", "Tidore Utara", "Tidore Selatan")
        ),
        "Papua" to mapOf(
            "Jayapura" to listOf("Jayapura Utara", "Jayapura Selatan", "Abepura", "Heram")
        ),
        "Papua Barat" to mapOf(
            "Manokwari" to listOf("Manokwari Barat", "Manokwari Timur", "Manokwari Utara", "Manokwari Selatan")
        ),
        "Papua Selatan" to mapOf(
            "Merauke" to listOf("Merauke", "Semangga", "Tanah Miring", "Kurik")
        ),
        "Papua Tengah" to mapOf(
            "Nabire" to listOf("Nabire", "Nabire Barat", "Teluk Kimi", "Wanggar")
        ),
        "Papua Pegunungan" to mapOf(
            "Jayawijaya" to listOf("Wamena", "Walelagama", "Asologaima", "Kurulu")
        ),
        "Papua Barat Daya" to mapOf(
            "Sorong" to listOf("Sorong", "Sorong Kota", "Sorong Timur", "Sorong Barat")
        )
    )

    fun provinces(): List<String> = DATA.keys.toList()

    fun cities(province: String?): List<String> =
        if (province.isNullOrBlank()) emptyList() else DATA[province]?.keys?.toList() ?: emptyList()

    fun districts(province: String?, city: String?): List<String> =
        if (province.isNullOrBlank() || city.isNullOrBlank()) emptyList()
        else DATA[province]?.get(city) ?: emptyList()
}
