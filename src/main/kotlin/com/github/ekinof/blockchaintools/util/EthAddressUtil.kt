package com.github.ekinof.blockchaintools.util

import org.bouncycastle.crypto.digests.KeccakDigest
import java.security.SecureRandom

object EthAddressUtil {

    private val secureRandom = SecureRandom()
    private val ADDRESS_REGEX = Regex("^0x[0-9a-fA-F]{40}$")

    fun isValidAddress(address: String): Boolean = ADDRESS_REGEX.matches(address)

    fun toChecksumAddress(address: String): String {
        val stripped = (if (address.startsWith("0x", ignoreCase = true)) address.substring(2) else address).lowercase()
        val hash = keccak256(stripped.toByteArray(Charsets.US_ASCII))
        val sb = StringBuilder("0x")
        for (i in stripped.indices) {
            val c = stripped[i]
            if (c in 'a'..'f') {
                val nibble = (hash[i / 2].toInt() ushr (if (i % 2 == 0) 4 else 0)) and 0xF
                sb.append(if (nibble >= 8) c.uppercaseChar() else c)
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun isValidChecksum(address: String): Boolean = address == toChecksumAddress(address)

    fun toggleCase(address: String): String {
        val checksummed = toChecksumAddress(address)
        return if (address == checksummed) {
            "0x" + address.removePrefix("0x").lowercase()
        } else {
            checksummed
        }
    }

    fun generateAddress(): String {
        val bytes = ByteArray(20)
        secureRandom.nextBytes(bytes)
        val hex = bytes.joinToString("") { "%02x".format(it) }
        return toChecksumAddress("0x$hex")
    }

    fun generateTxHash(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return "0x" + bytes.joinToString("") { "%02x".format(it) }
    }

    private fun keccak256(input: ByteArray): ByteArray {
        val digest = KeccakDigest(256)
        digest.update(input, 0, input.size)
        val out = ByteArray(32)
        digest.doFinal(out, 0)
        return out
    }
}
