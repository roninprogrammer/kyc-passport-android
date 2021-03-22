package org.jmrtd.cert

import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.cert.CRL
import java.security.cert.CRLSelector
import java.security.cert.CertSelector
import java.security.cert.CertStoreException
import java.security.cert.CertStoreParameters
import java.security.cert.CertStoreSpi
import java.security.cert.Certificate
import java.util.ArrayList
import java.util.Enumeration


class KeyStoreCertStoreSpi @Throws(InvalidAlgorithmParameterException::class)
constructor(params: CertStoreParameters) : CertStoreSpi(params) {

    private val keyStore: KeyStore = (params as KeyStoreCertStoreParameters).keyStore

    @Throws(CertStoreException::class)
    override fun engineGetCertificates(selector: CertSelector): Collection<Certificate> {
        try {
            val certificates = ArrayList<Certificate>(keyStore.size())
            val aliases = keyStore.aliases()
            while (aliases.hasMoreElements()) {
                val alias = aliases.nextElement() as String
                if (keyStore.isCertificateEntry(alias)) {
                    val certificate = keyStore.getCertificate(alias)
                    if (selector.match(certificate)) {
                        certificates.add(certificate)
                    }
                }
            }
            return certificates
        } catch (kse: KeyStoreException) {
            throw CertStoreException(kse.message)
        }

    }

    @Throws(CertStoreException::class)
    override fun engineGetCRLs(selector: CRLSelector): Collection<CRL> {
        return ArrayList(0)
    }
}