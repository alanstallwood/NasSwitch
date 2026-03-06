package com.alanstallwood.nasswitch.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class SecureKeyStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_nas_storage",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun savePrivateKey(key: String) {
        prefs.edit { putString("ssh_private_key", key) }
    }

    fun getPrivateKey(): String? {
        return try {
            prefs.getString("ssh_private_key", null)
        } catch (e: Exception) {
            null
        }
    }
}
