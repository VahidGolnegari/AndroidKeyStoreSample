package com.golnegari.keystoreexample.encryption

import android.content.Context
import android.opengl.GLException
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.math.BigInteger
import java.security.*
import java.util.*
import javax.security.auth.x500.X500Principal

class MyKeyStoreWrapper(private val context : Context) {

    private val keyStore : KeyStore = createAndroidKeySotre()

    private val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"

    private fun createAndroidKeySotre() : KeyStore {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore
    }

    fun createRsaKeyPair(alias : String) {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA" , "AndroidKeyStore")
        when {
            Build.VERSION.SDK_INT >= 23 -> {
                initSpecForApi23(alias , keyPairGenerator)
            }
            Build.VERSION.SDK_INT >= 18 -> {
                initSpecsForApi18(alias , keyPairGenerator)
            }
            else -> {

            }
        }
        keyPairGenerator.generateKeyPair()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun initSpecsForApi18(alias : String , generator : KeyPairGenerator) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.YEAR , 20);
        generator.initialize(KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias).setStartDate(startDate.time)
                .setEndDate(endDate.time)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(X500Principal("CN=${alias} CA")).build())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initSpecForApi23(alias : String, generator : KeyPairGenerator) {
        val spes = KeyGenParameterSpec.Builder(alias , KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
        generator.initialize(spes.build())
    }

    fun getRsaKeyPair(alias : String , password : String? = null) : KeyPair? {
        val privateKey = keyStore.getKey(alias , null) as PrivateKey?
        val publicKey = keyStore.getCertificate(alias)?.publicKey
        if (publicKey != null && privateKey != null) {
            return KeyPair(publicKey, privateKey)
        }
        return null
    }

}