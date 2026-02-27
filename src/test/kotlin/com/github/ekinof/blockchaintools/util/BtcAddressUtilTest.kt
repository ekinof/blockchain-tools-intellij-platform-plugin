package com.github.ekinof.blockchaintools.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BtcAddressUtilTest {

    // Test vectors for Bitcoin addresses
    private val validP2PKHAddresses = listOf(
        "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa", // Genesis block address
        "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2",
        "17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem"
    )

    private val validBech32Addresses = listOf(
        "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
        "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4"
    )

    @Test
    fun `isValidAddress accepts valid P2PKH addresses`() {
        for (addr in validP2PKHAddresses) {
            assertTrue("Expected valid P2PKH address: $addr", BtcAddressUtil.isValidAddress(addr))
        }
    }

    @Test
    fun `isValidAddress accepts valid Bech32 addresses`() {
        for (addr in validBech32Addresses) {
            assertTrue("Expected valid Bech32 address: $addr", BtcAddressUtil.isValidAddress(addr))
        }
    }

    @Test
    fun `isValidAddress rejects malformed addresses`() {
        assertFalse(BtcAddressUtil.isValidAddress(""))
        assertFalse(BtcAddressUtil.isValidAddress("1234"))
        assertFalse(BtcAddressUtil.isValidAddress("0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed")) // ETH address
        assertFalse(BtcAddressUtil.isValidAddress("bc1invalid"))
        assertFalse(BtcAddressUtil.isValidAddress("1234567890")) // too short
    }

    @Test
    fun `isValidAddress rejects addresses with invalid checksum`() {
        // Modified last char to break checksum
        val invalidChecksum = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNb"
        assertFalse(BtcAddressUtil.isValidAddress(invalidChecksum))
    }

    @Test
    fun `generateAddress returns a valid P2PKH Bitcoin address`() {
        repeat(10) {
            val addr = BtcAddressUtil.generateAddress()
            assertTrue("Expected valid address but got: $addr", BtcAddressUtil.isValidAddress(addr))
            assertTrue("Expected P2PKH address starting with 1, got: $addr", addr.startsWith("1"))
            assertTrue("Expected address length 26-35, got: ${addr.length}", addr.length in 26..35)
        }
    }

    @Test
    fun `generateTxHash returns a valid 64-char hex string`() {
        repeat(10) {
            val hash = BtcAddressUtil.generateTxHash()
            assertEquals("Expected 64 chars, got: $hash", 64, hash.length)
            assertTrue("Expected only hex chars, got: $hash", hash.all { it in '0'..'9' || it in 'a'..'f' })
        }
    }

    @Test
    fun `isValidTxHash accepts 64-char hex string`() {
        assertTrue(BtcAddressUtil.isValidTxHash("a".repeat(64)))
        assertTrue(BtcAddressUtil.isValidTxHash("0123456789abcdefABCDEF".repeat(2) + "0123456789abcdefABCD"))
        assertTrue(BtcAddressUtil.isValidTxHash("0000000000000000000000000000000000000000000000000000000000000000"))
    }

    @Test
    fun `isValidTxHash rejects malformed hashes`() {
        assertFalse(BtcAddressUtil.isValidTxHash("a".repeat(63)))   // too short
        assertFalse(BtcAddressUtil.isValidTxHash("a".repeat(65)))   // too long
        assertFalse(BtcAddressUtil.isValidTxHash("0x" + "a".repeat(64))) // with 0x prefix
        assertFalse(BtcAddressUtil.isValidTxHash("g".repeat(64)))   // invalid chars
        assertFalse(BtcAddressUtil.isValidTxHash(""))
    }

    @Test
    fun `generateAddress produces different addresses`() {
        val addresses = (1..100).map { BtcAddressUtil.generateAddress() }.toSet()
        assertEquals("Expected 100 unique addresses", 100, addresses.size)
    }

    @Test
    fun `generateTxHash produces different hashes`() {
        val hashes = (1..100).map { BtcAddressUtil.generateTxHash() }.toSet()
        assertEquals("Expected 100 unique hashes", 100, hashes.size)
    }
}
