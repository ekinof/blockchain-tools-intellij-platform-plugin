package com.github.ekinof.blockchaintools.util

import org.bouncycastle.crypto.digests.SHA256Digest
import java.security.SecureRandom

object BtcAddressUtil {

    private val secureRandom = SecureRandom()
    private const val BECH32_ALPHABET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"

    // Legacy P2PKH: 1...  (26-35 chars)
    private val P2PKH_REGEX = Regex("^1[a-km-zA-HJ-NP-Z1-9]{25,34}$")
    // SegWit P2WPKH: bc1... (42-62 chars for bech32)
    private val BECH32_REGEX = Regex("^(bc1|tb1)[a-z0-9]{39,59}$")
    // Transaction hash: 64 hex chars
    private val TXHASH_REGEX = Regex("^[0-9a-fA-F]{64}$")

    fun isValidAddress(address: String): Boolean {
        return when {
            P2PKH_REGEX.matches(address) -> isValidBase58Check(address)
            BECH32_REGEX.matches(address) -> isValidBech32(address)
            else -> false
        }
    }

    fun isValidTxHash(hash: String): Boolean = TXHASH_REGEX.matches(hash)

    fun generateAddress(): String {
        // Generate P2PKH address (1...)
        val bytes = ByteArray(20) // RIPEMD160 hash size
        secureRandom.nextBytes(bytes)
        return encodeBase58Check(0x00.toByte(), bytes)
    }

    fun generateTxHash(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun isValidBase58Check(address: String): Boolean {
        return try {
            val decoded = Base58Util.decode(address)
            if (decoded.size < 5) return false
            
            val payload = decoded.sliceArray(0 until decoded.size - 4)
            val checksum = decoded.sliceArray(decoded.size - 4 until decoded.size)
            val calculatedChecksum = doubleSha256(payload).sliceArray(0..3)
            
            checksum.contentEquals(calculatedChecksum)
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidBech32(address: String): Boolean {
        return try {
            val parts = address.lowercase().split("1")
            if (parts.size != 2) return false
            
            val hrp = parts[0]
            val data = parts[1]
            
            // Check if all characters are in bech32 alphabet
            data.all { it in BECH32_ALPHABET } && 
            // Check HRP
            (hrp == "bc" || hrp == "tb") &&
            // Verify checksum
            verifyBech32Checksum(hrp, data)
        } catch (e: Exception) {
            false
        }
    }

    private fun verifyBech32Checksum(hrp: String, data: String): Boolean {
        val values = data.map { BECH32_ALPHABET.indexOf(it) }
        val hrpExpanded = expandHrp(hrp)
        val combined = hrpExpanded + values
        return polymod(combined) == 1
    }

    private fun expandHrp(hrp: String): List<Int> {
        return hrp.map { it.code shr 5 } + listOf(0) + hrp.map { it.code and 31 }
    }

    private fun polymod(values: List<Int>): Int {
        val generator = listOf(0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3)
        var chk = 1
        for (value in values) {
            val top = chk shr 25
            chk = (chk and 0x1ffffff) shl 5 xor value
            for (i in 0..4) {
                if ((top shr i) and 1 != 0) {
                    chk = chk xor generator[i]
                }
            }
        }
        return chk
    }

    private fun encodeBase58Check(version: Byte, payload: ByteArray): String {
        val versionedPayload = byteArrayOf(version) + payload
        val checksum = doubleSha256(versionedPayload).sliceArray(0..3)
        val addressBytes = versionedPayload + checksum
        return Base58Util.encode(addressBytes)
    }

    private fun doubleSha256(input: ByteArray): ByteArray {
        return sha256(sha256(input))
    }

    private fun sha256(input: ByteArray): ByteArray {
        val digest = SHA256Digest()
        digest.update(input, 0, input.size)
        val out = ByteArray(32)
        digest.doFinal(out, 0)
        return out
    }
}
