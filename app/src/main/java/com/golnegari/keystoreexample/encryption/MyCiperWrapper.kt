package com.golnegari.keystoreexample.encryption

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher

class MyCiperWrapper(algoritmType : String) {

    private val cipher = Cipher.getInstance(algoritmType)

    companion object {
        val RSA = "RSA/ECB/PKCS1Padding"
    }

    fun encrypt(data : String , key : Key?) : String {
        cipher.init(Cipher.ENCRYPT_MODE , key)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedData , Base64.DEFAULT)
    }

    fun decrypt(data: String , key : Key?) : String {
        cipher.init(Cipher.DECRYPT_MODE , key)
        val dataByte = Base64.decode(data , Base64.DEFAULT)
        return String(cipher.doFinal(dataByte))
    }
}