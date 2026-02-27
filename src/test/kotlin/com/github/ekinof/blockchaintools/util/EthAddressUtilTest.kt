package com.github.ekinof.blockchaintools.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EthAddressUtilTest {

    // EIP-55 test vectors from the specification
    private val vectors = listOf(
        "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed",
        "0xfB6916095ca1df60bB79Ce92cE3Ea74c37c5d359",
        "0xdbF03B407c01E7cD3CBea99509d93f8DDDC8C6FB",
        "0xD1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb"
    )

    @Test
    fun `toChecksumAddress produces correct EIP-55 output for all spec vectors`() {
        for (v in vectors) {
            assertEquals(v, EthAddressUtil.toChecksumAddress(v.lowercase()))
            assertEquals(v, EthAddressUtil.toChecksumAddress(v.uppercase()))
            assertEquals(v, EthAddressUtil.toChecksumAddress(v))
        }
    }

    @Test
    fun `isValidChecksum returns true for correctly checksummed address`() {
        for (v in vectors) {
            assertTrue(EthAddressUtil.isValidChecksum(v))
        }
    }

    @Test
    fun `isValidChecksum returns false for lowercase address`() {
        assertFalse(EthAddressUtil.isValidChecksum(vectors[0].lowercase()))
    }

    @Test
    fun `isValidAddress accepts valid hex addresses regardless of case`() {
        assertTrue(EthAddressUtil.isValidAddress("0x5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"))
        assertTrue(EthAddressUtil.isValidAddress(vectors[0]))
    }

    @Test
    fun `isValidAddress rejects malformed addresses`() {
        assertFalse(EthAddressUtil.isValidAddress("5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"))  // no 0x
        assertFalse(EthAddressUtil.isValidAddress("0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAe"))   // too short
        assertFalse(EthAddressUtil.isValidAddress("0xGGGG"))                                       // invalid chars
        assertFalse(EthAddressUtil.isValidAddress(""))
    }

    @Test
    fun `toggleCase converts checksummed to lowercase`() {
        val checksummed = vectors[0]
        val lower = "0x" + checksummed.removePrefix("0x").lowercase()
        assertEquals(lower, EthAddressUtil.toggleCase(checksummed))
    }

    @Test
    fun `toggleCase converts lowercase to checksummed`() {
        val checksummed = vectors[0]
        val lower = "0x" + checksummed.removePrefix("0x").lowercase()
        assertEquals(checksummed, EthAddressUtil.toggleCase(lower))
    }

    @Test
    fun `generateAddress returns a valid checksummed Ethereum address`() {
        repeat(10) {
            val addr = EthAddressUtil.generateAddress()
            assertTrue("Expected valid address but got: $addr", EthAddressUtil.isValidAddress(addr))
            assertTrue("Expected checksummed address but got: $addr", EthAddressUtil.isValidChecksum(addr))
        }
    }

    @Test
    fun `generateTxHash returns a valid 0x-prefixed 64-char lowercase hex string`() {
        repeat(10) {
            val hash = EthAddressUtil.generateTxHash()
            assertTrue("Expected 0x prefix, got: $hash", hash.startsWith("0x"))
            assertEquals("Expected 66 chars total, got: $hash", 66, hash.length)
            val hex = hash.removePrefix("0x")
            assertTrue("Expected only hex chars, got: $hex", hex.all { it in '0'..'9' || it in 'a'..'f' })
        }
    }

    @Test
    fun `isValidTxHash accepts 0x-prefixed 64-char hex string`() {
        assertTrue(EthAddressUtil.isValidTxHash("0x" + "a".repeat(64)))
        assertTrue(EthAddressUtil.isValidTxHash("0x" + "0123456789abcdefABCDEF".repeat(2) + "01234567890123456789"))
    }

    @Test
    fun `isValidTxHash rejects malformed hashes`() {
        assertFalse(EthAddressUtil.isValidTxHash("0x" + "a".repeat(63)))   // too short
        assertFalse(EthAddressUtil.isValidTxHash("0x" + "a".repeat(65)))   // too long
        assertFalse(EthAddressUtil.isValidTxHash("a".repeat(64)))           // no 0x prefix
        assertFalse(EthAddressUtil.isValidTxHash("0x" + "g".repeat(64)))   // invalid chars
        assertFalse(EthAddressUtil.isValidTxHash(""))
    }
}
