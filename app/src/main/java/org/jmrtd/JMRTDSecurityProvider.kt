package org.jmrtd

import java.security.Provider
import java.security.Security
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.logging.Logger



class JMRTDSecurityProvider private constructor() : Provider("JMRTD", 0.1, "JMRTD Security Provider") {

    init {
        put("CertificateFactory.CVC", "org.jmrtd.cert.CVCertificateFactorySpi")
        put("CertStore.PKD", "org.jmrtd.cert.PKDCertStoreSpi")
        put("CertStore.JKS", "org.jmrtd.cert.KeyStoreCertStoreSpi")
        put("CertStore.BKS", "org.jmrtd.cert.KeyStoreCertStoreSpi")
        put("CertStore.PKCS12", "org.jmrtd.cert.KeyStoreCertStoreSpi")

        if (BC_PROVIDER != null) {
            /* Replicate BC algorithms... */

            /* FIXME: this won't work, our provider is not signed! */
            //			replicateFromProvider("Cipher", "DESede/CBC/NoPadding", getBouncyCastleProvider());
            //			replicateFromProvider("Cipher", "RSA/ECB/PKCS1Padding", getBouncyCastleProvider());
            //			replicateFromProvider("Cipher", "RSA/NONE/NoPadding", getBouncyCastleProvider());
            //			replicateFromProvider("KeyFactory", "RSA", getBouncyCastleProvider());
            //			replicateFromProvider("KeyFactory", "DH", getBouncyCastleProvider());
            //			replicateFromProvider("Mac", "ISO9797ALG3MAC", getBouncyCastleProvider());
            //			replicateFromProvider("Mac", "ISO9797ALG3WITHISO7816-4PADDING", getBouncyCastleProvider());
            //			replicateFromProvider("SecretKeyFactory", "DESede", getBouncyCastleProvider());

            /* But these work fine. */
            replicateFromProvider("CertificateFactory", "X.509", bouncyCastleProvider!!)
            replicateFromProvider("CertStore", "Collection", bouncyCastleProvider!!)
            //			replicateFromProvider("KeyStore", "JKS", SUN_PROVIDER);
            replicateFromProvider("MessageDigest", "SHA1", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA1withRSA/ISO9796-2", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "MD2withRSA", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "MD4withRSA", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "MD5withRSA", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA1withRSA", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA1withRSA/ISO9796-2", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA256withRSA", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA256withRSA/ISO9796-2", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA384withRSA", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA384withRSA/ISO9796-2", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA512withRSA", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA512withRSA/ISO9796-2", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA224withRSA", bouncyCastleProvider!!)
            replicateFromProvider("Signature", "SHA224withRSA/ISO9796-2", bouncyCastleProvider!!)

            replicateFromProvider("Signature", "SHA256withRSA/PSS", bouncyCastleProvider!!)


            /* Testing 0.4.7 -- MO */
            //			replicateFromProvider("KeyStore", "UBER", getBouncyCastleProvider());
            //			replicateFromProvider("KeyPairGenerator", "ECDHC", getBouncyCastleProvider());
            //			replicateFromProvider("KeyPairGenerator", "ECDSA", getBouncyCastleProvider());
            //			replicateFromProvider("X509StreamParser", "CERTIFICATE", getBouncyCastleProvider());

            put("Alg.Alias.Mac.ISO9797Alg3Mac", "ISO9797ALG3MAC")
            put("Alg.Alias.CertificateFactory.X509", "X.509")
        }
    }

    private fun replicateFromProvider(serviceName: String, algorithmName: String, provider: Provider) {
        val name = "$serviceName.$algorithmName"
        val service = provider[name]
        if (service != null) {
            put(name, service)
        }
    }

    companion object {

        private val serialVersionUID = -2881416441551680704L

        private val LOGGER = Logger.getLogger("org.jmrtd")

        private val SUN_PROVIDER_CLASS_NAME = "sun.security.provider.Sun"
        private val BC_PROVIDER_CLASS_NAME = "org.bouncycastle.jce.provider.BouncyCastleProvider"
        private val SC_PROVIDER_CLASS_NAME = "org.spongycastle.jce.provider.BouncyCastleProvider"

        //	private static final Provider SUN_PROVIDER = null; // getProviderOrNull(SUN_PROVIDER_CLASS_NAME);
        private val BC_PROVIDER = org.bouncycastle.jce.provider.BouncyCastleProvider()
        //			getProviderOrNull(BC_PROVIDER_CLASS_NAME);
        private val SC_PROVIDER = org.spongycastle.jce.provider.BouncyCastleProvider()
        //			getProviderOrNull(SC_PROVIDER_CLASS_NAME);
        val instance: Provider = JMRTDSecurityProvider()

        init {
            Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
            /*
				if (BC_PROVIDER != null) { Security.insertProviderAt(BC_PROVIDER, 1); }
				if (SC_PROVIDER != null) { Security.insertProviderAt(SC_PROVIDER, 2); }
				if (JMRTD_PROVIDER != null) { Security.insertProviderAt(JMRTD_PROVIDER, 3); }*/
        }

        /**
         * Temporarily puts the BC provider on number one in the list of
         * providers, until caller calls [.endPreferBouncyCastleProvider].
         *
         * @return the index of BC, if it was present, in the list of providers
         *
         * @see .endPreferBouncyCastleProvider
         */
        fun beginPreferBouncyCastleProvider(): Int {
            val bcProvider = bouncyCastleProvider ?: return -1
            val providers = Security.getProviders()
            for (i in providers.indices) {
                val provider = providers[i]
                if (bcProvider.javaClass.canonicalName == provider.javaClass.canonicalName) {
                    Security.removeProvider(provider.name)
                    Security.insertProviderAt(bcProvider, 1)
                    return i + 1
                }
            }
            return -1
        }

        /**
         * Removes the BC provider from the number one position and puts it back
         * at its original position, after a call to [.beginPreferBouncyCastleProvider].
         *
         * @param i the original index of the BC provider
         *
         * @see .beginPreferBouncyCastleProvider
         */
        fun endPreferBouncyCastleProvider(i: Int) {
            val bcProvider = bouncyCastleProvider
            Security.removeProvider(bcProvider!!.name)
            if (i > 0) {
                Security.insertProviderAt(bcProvider, i)
            }
        }

        /**
         * Gets the BC provider, if present.
         *
         * @return the BC provider, the SC provider, or `null`
         */
        val bouncyCastleProvider: Provider?
            get() {
                if (BC_PROVIDER != null) {
                    return BC_PROVIDER
                }
                if (SC_PROVIDER != null) {
                    return SC_PROVIDER
                }
                LOGGER.severe("No Bouncy or Spongy provider")
                return null
            }

        /**
         * Gets the SC provider, if present.
         *
         * @return the SC provider, the BC provider, or `null`
         */
        val spongyCastleProvider: Provider?
            get() {
                if (SC_PROVIDER != null) {
                    return SC_PROVIDER
                }
                if (BC_PROVIDER != null) {
                    return BC_PROVIDER
                }
                LOGGER.severe("No Bouncy or Spongy provider")
                return null
            }

        private fun getProvider(serviceName: String, algorithmName: String): Provider? {
            val providers = getProviders(serviceName, algorithmName)
            return if (providers != null && providers.size > 0) {
                providers[0]
            } else null
        }

        private fun getProviders(serviceName: String, algorithmName: String): List<Provider>? {
            if (Security.getAlgorithms(serviceName).contains(algorithmName)) {
                val providers = Security.getProviders("$serviceName.$algorithmName")
                return ArrayList(Arrays.asList(*providers))
            }
            if (BC_PROVIDER != null && BC_PROVIDER.getService(serviceName, algorithmName) != null) {
                return ArrayList(listOf<Provider>(BC_PROVIDER))
            }
            if (SC_PROVIDER != null && SC_PROVIDER.getService(serviceName, algorithmName) != null) {
                return ArrayList(listOf<Provider>(SC_PROVIDER))
            }
            return if (instance != null && instance.getService(serviceName, algorithmName) != null) {
                ArrayList(listOf(instance))
            } else null
        }
    }
}