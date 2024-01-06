package com.overdevx.sibokas_xml.data.API
import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyProperties
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Calendar
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

object TokenManager {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "YourKeyAlias"

    fun generateKeyPair(context: Context) {
        if (!keyPairExists()) {
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            endDate.add(Calendar.YEAR, 25)

            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                ANDROID_KEYSTORE
            )
            val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(KEY_ALIAS)
                .setSubject(X500Principal("CN=$KEY_ALIAS"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(startDate.time)
                .setEndDate(endDate.time)
                .build()

            keyPairGenerator.initialize(spec)
            keyPairGenerator.generateKeyPair()
        }
    }

    private fun keyPairExists(): Boolean {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        return keyStore.containsAlias(KEY_ALIAS)
    }

    fun encryptText(text: String): ByteArray {
        val publicKey = getPublicKey()
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        return cipher.doFinal(text.toByteArray())
    }

    fun decryptData(encryptedData: ByteArray): String {
        val privateKey = getPrivateKey()
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        return String(cipher.doFinal(encryptedData))
    }

    private fun getPrivateKey(): PrivateKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        return keyStore.getKey(KEY_ALIAS, null) as PrivateKey
    }

    private fun getPublicKey(): PublicKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        return keyStore.getCertificate(KEY_ALIAS).publicKey
    }

}



