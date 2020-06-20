package com.golnegari.keystoreexample.encryption

import android.content.Context
import org.spongycastle.jcajce.provider.asymmetric.RSA

class MyEncryption(context: Context) {

    private val keystoreWrapper = MyKeyStoreWrapper(context)
    private val RSA_ALIAS = "MyAliasKey"

    init {
        keystoreWrapper.createRsaKeyPair(RSA_ALIAS)
    }


    fun encrypt(data : String) : String{
        val kp = keystoreWrapper.getRsaKeyPair(RSA_ALIAS)
       return MyCiperWrapper(MyCiperWrapper.RSA).encrypt(data , kp?.public)
    }

    fun decrypt(data : String) : String {
        val kp = keystoreWrapper.getRsaKeyPair(RSA_ALIAS)
        return MyCiperWrapper(MyCiperWrapper.RSA).decrypt(data , kp?.private)
    }

}