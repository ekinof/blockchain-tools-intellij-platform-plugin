package com.github.ekinof.blockchaintools.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SolAddressUtilTest {

    // Test vectors for Solana addresses (Base58 encoded 32-byte public keys)
    private val validAddresses = listOf(
        "11111111111111111111111111111111", // System program
        "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", // Token program
        "SysvarRent111111111111111111111111111111111",
        "9xQeWvG816bUx9EPjHmaT23yvVM2ZWbrrpZb9PusVFin" // Serum program
    )

    private val validSignatures = listOf(
        "5VERv8NMvzbJMEkV8xnrLkEaWRtSz9CosKDYjCJjBRnbJLgp8uirBgmQpjKhoR4tjF3ZpRzrFmBV6UjKdiSZkQUW",
        "3yMKPbJhqYLWWrW4vwmz8NQ6Y8qxHKgGbJm9vDu3xQzKtT2b8dR9fHyFaQnZqRxJ8vSk2xQpV3nT7mYwZ4bK5cHp"
    )

    @Test
    fun `isValidAddress accepts valid Solana addresses`() {
        for (addr in validAddresses) {
            assertTrue("Expected valid Solana address: $addr", SolAddressUtil.isValidAddress(addr))
        }
    }

    @Test
    fun `isValidAddress rejects malformed addresses`() {
        assertFalse(SolAddressUtil.isValidAddress(""))
        assertFalse(SolAddressUtil.isValidAddress("1234"))
        assertFalse(SolAddressUtil.isValidAddress("0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed")) // ETH address
        assertFalse(SolAddressUtil.isValidAddress("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa")) // BTC address
        assertFalse(SolAddressUtil.isValidAddress("invalidO0Il")) // contains invalid Base58 chars (O, 0, I, l)
    }

    @Test
    fun `isValidAddress rejects addresses with wrong length`() {
        // Too short (only 20 chars)
        assertFalse(SolAddressUtil.isValidAddress("1111111111111111111"))
        // Too long (50 chars)
        assertFalse(SolAddressUtil.isValidAddress("1".repeat(50)))
    }

    @Test
    fun `generateAddress returns a valid Solana address`() {
        repeat(10) {
            val addr = SolAddressUtil.generateAddress()
            assertTrue("Expected valid address but got: $addr", SolAddressUtil.isValidAddress(addr))
            assertTrue("Expected address length 32-44, got: ${addr.length}", addr.length in 32..44)
            assertTrue("Expected Base58 chars only", addr.all { it in '1'..'9' || it in 'A'..'Z' || it in 'a'..'z' })
            assertFalse("Should not contain O", addr.contains('O'))
            assertFalse("Should not contain 0", addr.contains('0'))
            assertFalse("Should not contain I", addr.contains('I'))
            assertFalse("Should not contain l", addr.contains('l'))
        }
    }

    @Test
    fun `isValidSignature accepts valid Solana signatures`() {
        for (sig in validSignatures) {
            assertTrue("Expected valid Solana signature: $sig", SolAddressUtil.isValidSignature(sig))
        }
    }

    @Test
    fun `isValidSignature rejects malformed signatures`() {
        assertFalse(SolAddressUtil.isValidSignature(""))
        assertFalse(SolAddressUtil.isValidSignature("1234"))
        assertFalse(SolAddressUtil.isValidSignature("invalidO0Il"))
        assertFalse(SolAddressUtil.isValidSignature("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")) // too short for signature
    }

    @Test
    fun `generateSignature returns a valid Solana signature`() {
        repeat(10) {
            val sig = SolAddressUtil.generateSignature()
            assertTrue("Expected valid signature but got: $sig", SolAddressUtil.isValidSignature(sig))
            assertTrue("Expected signature length 87-88, got: ${sig.length}", sig.length in 87..88)
            assertTrue("Expected Base58 chars only", sig.all { it in '1'..'9' || it in 'A'..'Z' || it in 'a'..'z' })
            assertFalse("Should not contain O", sig.contains('O'))
            assertFalse("Should not contain 0", sig.contains('0'))
            assertFalse("Should not contain I", sig.contains('I'))
            assertFalse("Should not contain l", sig.contains('l'))
        }
    }

    @Test
    fun `generateAddress produces different addresses`() {
        val addresses = (1..100).map { SolAddressUtil.generateAddress() }.toSet()
        assertEquals("Expected 100 unique addresses", 100, addresses.size)
    }

    @Test
    fun `generateSignature produces different signatures`() {
        val signatures = (1..100).map { SolAddressUtil.generateSignature() }.toSet()
        assertEquals("Expected 100 unique signatures", 100, signatures.size)
    }
}
