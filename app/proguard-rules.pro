# Add project specific ProGuard rules here.
# ─────────────────────────────────────────────────────────────────────────────

# ── Room ──────────────────────────────────────────────────────────────────────
-keep class androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# ── SaveBite Data Models ───────────────────────────────────────────────────────
-keep class com.example.data.local.entity.** { *; }
-keep class com.example.data.local.converter.** { *; }
-keep class com.example.data.model.** { *; }
-keep enum com.example.data.model.** { *; }

# ── Retrofit + OkHttp ─────────────────────────────────────────────────────────
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ── Moshi ─────────────────────────────────────────────────────────────────────
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}

# ── Coil ──────────────────────────────────────────────────────────────────────
-dontwarn coil.**

# ── Firebase ──────────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ── Kotlin Coroutines ─────────────────────────────────────────────────────────
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ── Crash Reporting: Preserve stack traces ────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── DataStore ─────────────────────────────────────────────────────────────────
-keep class androidx.datastore.** { *; }

# ── Jetpack Compose ───────────────────────────────────────────────────────────
-dontwarn androidx.compose.**

