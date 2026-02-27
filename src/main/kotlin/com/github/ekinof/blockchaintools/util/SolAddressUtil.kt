package com.github.ekinof.blockchaintools.util

import java.security.SecureRandom

object SolAddressUtil {

    private val secureRandom = SecureRandom()
    
    // Solana addresses are Base58 encoded public keys (32 bytes -> 32-44 chars)
    private val ADDRESS_REGEX = Regex("^[1-9A-HJ-NP-Za-km-z]{32,44}$")
    // Solana transaction signatures are Base58 encoded (64 bytes -> 87-88 chars typically)
    private val SIGNATURE_REGEX = Regex("^[1-9A-HJ-NP-Za-km-z]{87,88}$")

    fun isValidAddress(address: String): Boolean {
        if (!ADDRESS_REGEX.matches(address)) return false
        return try {
            val decoded = Base58Util.decode(address)
            decoded.size == 32
        } catch (e: Exception) {
            false
        }
    }

    fun isValidSignature(signature: String): Boolean {
        if (!SIGNATURE_REGEX.matches(signature)) return false
        return try {
            val decoded = Base58Util.decode(signature)
            decoded.size == 64
        } catch (e: Exception) {
            false
        }
    }

    fun generateAddress(): String {
        val bytes = ByteArray(32) // Ed25519 public key size
        secureRandom.nextBytes(bytes)
        return Base58Util.encode(bytes)
    }

    fun generateSignature(): String {
        val bytes = ByteArray(64) // Ed25519 signature size
        secureRandom.nextBytes(bytes)
        return Base58Util.encode(bytes)
    }
}
