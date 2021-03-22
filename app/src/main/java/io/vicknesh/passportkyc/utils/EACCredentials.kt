package io.vicknesh.passportkyc.utils
import java.security.PrivateKey
import java.security.cert.Certificate

class EACCredentials
/**
 * Creates EAC credentials.
 *
 * @param privateKey
 * @param chain
 */
    (val privateKey: PrivateKey, val chain: Array<Certificate>)