package com.bloodconnect.app.data

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Firebase-backed data layer.
 *
 * - Authentication: Firebase Auth (email/password).
 * - Storage: Cloud Firestore collection "donors" (1 document per donor, id = auth UID).
 * - A realtime snapshot listener keeps [donors] in sync across all devices.
 *
 * Domain logic (eligibility, blood compatibility, region proximity ranking) runs
 * on the in-memory snapshot that Firestore streams to us.
 */
object Repository {

    const val DONATION_GAP_DAYS = 75

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val donorsCol get() = db.collection("donors")

    private val _donors = MutableStateFlow<List<Donor>>(emptyList())
    val donors: StateFlow<List<Donor>> = _donors.asStateFlow()

    private val _session = MutableStateFlow<Donor?>(null)
    val session: StateFlow<Donor?> = _session.asStateFlow()

    private var started = false

    /** Call once on app start: attach the realtime listener, restore session, seed sample data. */
    fun start() {
        if (started) return
        started = true

        donorsCol.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            val list = snapshot.documents.mapNotNull { it.toObject(Donor::class.java) }
            _donors.value = list
            if (list.isEmpty()) seedIfEmpty()
            // Keep the logged-in profile fresh when it changes on another device.
            val uid = auth.currentUser?.uid
            if (uid != null) list.find { it.id == uid }?.let { _session.value = it }
        }

        // Restore an existing session after app restart.
        auth.currentUser?.uid?.let { uid ->
            donorsCol.document(uid).get().addOnSuccessListener { doc ->
                doc.toObject(Donor::class.java)?.let { _session.value = it }
            }
        }
    }

    private var seeding = false
    private fun seedIfEmpty() {
        if (seeding) return
        seeding = true
        donorsCol.limit(1).get().addOnSuccessListener { qs ->
            if (qs.isEmpty) {
                // Fixed doc IDs make seeding idempotent even if two devices seed at once.
                seedDonors().forEach { d -> donorsCol.document(d.id).set(d) }
            }
        }
    }

    fun getDonor(id: String): Donor? = _donors.value.find { it.id == id }

    // ---- Diagnostics ----
    fun projectId(): String = try {
        FirebaseApp.getInstance().options.projectId ?: "(null)"
    } catch (e: Exception) {
        "ERROR: ${e.message}"
    }

    /** Force a SERVER read so connection problems surface immediately instead of reading the offline cache. */
    fun testServer(onResult: (String) -> Unit) {
        donorsCol.get(Source.SERVER)
            .addOnSuccessListener { qs -> onResult("OK terhubung. ${qs.size()} dokumen di server.") }
            .addOnFailureListener { e -> onResult("GAGAL: ${e.javaClass.simpleName}: ${e.localizedMessage}") }
    }

    // ---- Auth ----
    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        bloodType: String,
        lastDonation: String?,
        province: String,
        city: String,
        district: String,
        note: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val cleanEmail = email.trim()
        auth.createUserWithEmailAndPassword(cleanEmail, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) { onError("Gagal membuat akun."); return@addOnSuccessListener }
                val donor = Donor(
                    id = uid,
                    name = name.trim(),
                    email = cleanEmail,
                    phone = phone.trim(),
                    bloodType = bloodType,
                    available = true,
                    lastDonation = lastDonation?.ifBlank { null },
                    location = Location("Indonesia", province, city, district),
                    note = note.trim()
                )
                donorsCol.document(uid).set(donor)
                    .addOnSuccessListener { _session.value = donor; onSuccess() }
                    .addOnFailureListener { e -> onError(e.localizedMessage ?: "Gagal menyimpan profil.") }
            }
            .addOnFailureListener { e -> onError(mapAuthError(e)) }
    }

    fun login(
        email: String,
        password: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) { onError("Gagal masuk."); return@addOnSuccessListener }
                donorsCol.document(uid).get()
                    .addOnSuccessListener { doc ->
                        val donor = doc.toObject(Donor::class.java)
                        if (donor != null) { _session.value = donor; onSuccess() }
                        else onError("Profil donor tidak ditemukan.")
                    }
                    .addOnFailureListener { e -> onError(e.localizedMessage ?: "Gagal memuat profil.") }
            }
            .addOnFailureListener { e -> onError(mapAuthError(e)) }
    }

    fun logout() {
        auth.signOut()
        _session.value = null
    }

    fun saveDonor(donor: Donor) {
        donorsCol.document(donor.id).set(donor)
        if (_session.value?.id == donor.id) _session.value = donor
    }

    private fun mapAuthError(e: Exception): String {
        val m = e.localizedMessage ?: "Terjadi kesalahan."
        return when {
            m.contains("already in use", true) -> "Email sudah terdaftar. Silakan login."
            m.contains("password is invalid", true) ||
                m.contains("credential is incorrect", true) ||
                m.contains("INVALID_LOGIN", true) -> "Email atau password salah."
            m.contains("no user record", true) -> "Akun tidak ditemukan. Silakan daftar dulu."
            m.contains("badly formatted", true) -> "Format email tidak valid."
            m.contains("least 6 characters", true) -> "Password minimal 6 karakter."
            m.contains("network error", true) -> "Tidak ada koneksi internet."
            else -> m
        }
    }

    // ---- Eligibility ----
    fun daysSince(dateStr: String?): Int {
        if (dateStr.isNullOrBlank()) return Int.MAX_VALUE
        return try {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val parsed = fmt.parse(dateStr) ?: return Int.MAX_VALUE
            ((Date().time - parsed.time) / 86_400_000L).toInt()
        } catch (e: Exception) {
            Int.MAX_VALUE
        }
    }

    fun isEligible(donor: Donor): Boolean = daysSince(donor.lastDonation) >= DONATION_GAP_DAYS

    fun nextEligibleDays(donor: Donor): Int {
        val left = DONATION_GAP_DAYS - daysSince(donor.lastDonation)
        return if (left > 0) left else 0
    }

    // ---- Proximity ranking by administrative region ----
    fun proximityTier(loc: Location, target: Location?): Int {
        if (target == null || target.province.isBlank()) return 0
        if (loc.province == target.province && loc.city == target.city && loc.district == target.district) return 1
        if (loc.province == target.province && loc.city == target.city) return 2
        if (loc.province == target.province) return 3
        if (loc.country == target.country) return 4
        return 5
    }

    fun tierLabel(tier: Int): String = when (tier) {
        1 -> "Satu kecamatan"
        2 -> "Satu kota/kabupaten"
        3 -> "Satu provinsi"
        4 -> "Dalam negeri"
        5 -> "Luar negeri"
        else -> ""
    }

    // ---- Search ----
    fun search(
        recipientType: String?,
        bloodType: String?,
        province: String?,
        city: String?,
        district: String?,
        onlyAvailable: Boolean,
        excludeId: String?
    ): List<DonorMatch> {
        val target = if (!province.isNullOrBlank())
            Location("Indonesia", province, city ?: "", district ?: "")
        else null

        val filtered = _donors.value.filter { d ->
            if (excludeId != null && d.id == excludeId) return@filter false
            if (onlyAvailable && !d.available) return@filter false
            if (!recipientType.isNullOrBlank()) {
                val ok = Regions.COMPATIBILITY[recipientType]?.contains(d.bloodType) ?: false
                if (!ok) return@filter false
            } else if (!bloodType.isNullOrBlank()) {
                if (d.bloodType != bloodType) return@filter false
            }
            true
        }

        return filtered
            .map { DonorMatch(it, proximityTier(it.location, target)) }
            .sortedWith(
                compareBy<DonorMatch> { if (it.tier == 0) 99 else it.tier }
                    .thenByDescending { it.donor.available }
                    .thenByDescending { daysSince(it.donor.lastDonation) }
            )
    }

    private fun seedDonors(): List<Donor> = listOf(
        Donor("d1", "Andini Pratiwi", "andini@example.com", "081234567001", "O+", true, "2026-02-10",
            Location("Indonesia", "Sumatera Barat", "Padang", "Padang Barat"), "Siap dihubungi kapan saja"),
        Donor("d2", "Budi Santoso", "budi@example.com", "081234567002", "A+", true, "2026-01-05",
            Location("Indonesia", "Sumatera Barat", "Padang", "Padang Timur"), ""),
        Donor("d3", "Citra Lestari", "citra@example.com", "081234567003", "B+", true, null,
            Location("Indonesia", "Sumatera Barat", "Bukittinggi", "Guguk Panjang"), "Donor rutin"),
        Donor("d4", "Dewa Kurnia", "dewa@example.com", "081234567004", "AB+", false, "2026-05-20",
            Location("Indonesia", "DKI Jakarta", "Jakarta Selatan", "Tebet"), ""),
        Donor("d5", "Eka Saputra", "eka@example.com", "081234567005", "O-", true, "2026-04-25",
            Location("Indonesia", "Sumatera Barat", "Padang", "Kuranji"), ""),
        Donor("d6", "Fitri Handayani", "fitri@example.com", "081234567006", "A-", true, "2025-12-01",
            Location("Indonesia", "Jawa Barat", "Bandung", "Coblong"), "Bersedia akhir pekan"),
        Donor("d7", "Gilang Ramadhan", "gilang@example.com", "081234567007", "O+", true, "2026-03-15",
            Location("Indonesia", "Sumatera Barat", "Padang", "Lubuk Begalung"), ""),
        Donor("d8", "Hana Wijaya", "hana@example.com", "081234567008", "B-", true, null,
            Location("Indonesia", "Jawa Timur", "Surabaya", "Gubeng"), ""),
        Donor("d9", "Irfan Maulana", "irfan@example.com", "081234567009", "O+", true, "2026-01-30",
            Location("Indonesia", "Sumatera Barat", "Padang", "Padang Utara"), ""),
        Donor("d10", "Joko Susilo", "joko@example.com", "081234567010", "AB-", true, "2025-11-11",
            Location("Indonesia", "Yogyakarta", "Kota Yogyakarta", "Umbulharjo"), "")
    )
}
