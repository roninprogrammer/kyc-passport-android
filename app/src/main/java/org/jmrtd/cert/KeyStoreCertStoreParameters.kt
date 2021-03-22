package org.jmrtd.cert

import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URLConnection
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.cert.CertStoreParameters
import java.util.logging.Logger

import org.jmrtd.JMRTDSecurityProvider


class KeyStoreCertStoreParameters(val keyStore: KeyStore) : Cloneable, CertStoreParameters {

    @Throws(KeyStoreException::class)
    constructor(uri: URI, password: CharArray) : this(uri, DEFAULT_ALGORITHM, password)

    @Throws(KeyStoreException::class)
    @JvmOverloads
    constructor(uri: URI, algorithm: String = DEFAULT_ALGORITHM, password: CharArray = DEFAULT_PASSWORD) : this(readKeyStore(uri, algorithm, password))

    /**
     * Makes a shallow copy of this object as this
     * class is immutable.
     *
     * @return a shallow copy of this object
     */
    override fun clone(): Any {
        return KeyStoreCertStoreParameters(keyStore)
    }

    companion object {

        private val LOGGER = Logger.getLogger("org.jmrtd")

        private val DEFAULT_ALGORITHM = "JKS"
        private val DEFAULT_PASSWORD = "".toCharArray()

        @Throws(KeyStoreException::class)
        private fun readKeyStore(location: URI, keyStoreType: String, password: CharArray): KeyStore {
            try {
                val n = JMRTDSecurityProvider.beginPreferBouncyCastleProvider()
                val uc = location.toURL().openConnection()
                val inputStream = uc.getInputStream()
                var ks: KeyStore? = null
                ks = KeyStore.getInstance(keyStoreType)
                try {
                    LOGGER.info("KeystoreCertStore will use provider for KeyStore: " + ks!!.provider.javaClass.canonicalName!!)
                    ks.load(inputStream, password)
                } catch (ioe: IOException) {
                    LOGGER.warning("Cannot read this file \"$location\" as keystore")
                    // ioe.printStackTrace();
                }

                inputStream.close()
                JMRTDSecurityProvider.endPreferBouncyCastleProvider(n)
                return ks
            } catch (e: Exception) {
                // e.printStackTrace();
                throw KeyStoreException("Error getting keystore: " + e.message)
            }

        }
    }
}