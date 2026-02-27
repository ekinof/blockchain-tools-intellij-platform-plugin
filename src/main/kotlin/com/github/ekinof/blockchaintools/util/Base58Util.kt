package com.github.ekinof.blockchaintools.util

object Base58Util {

    private const val BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

    fun encode(input: ByteArray): String {
        if (input.isEmpty()) return ""
        
        // Count leading zeros
        var leadingZeros = 0
        while (leadingZeros < input.size && input[leadingZeros] == 0.toByte()) {
            leadingZeros++
        }
        
        // Convert to base58
        val digits = mutableListOf<Int>()
        for (byte in input) {
            var carry = byte.toInt() and 0xff
            for (i in digits.indices.reversed()) {
                carry += digits[i] shl 8
                digits[i] = carry % 58
                carry /= 58
            }
            while (carry > 0) {
                digits.add(0, carry % 58)
                carry /= 58
            }
        }
        
        // Skip leading zeros in digits
        var startAt = 0
        while (startAt < digits.size && digits[startAt] == 0) {
            startAt++
        }
        
        // Convert to base58 string
        return "1".repeat(leadingZeros) + digits.subList(startAt, digits.size).map { BASE58_ALPHABET[it] }.joinToString("")
    }

    fun decode(input: String): ByteArray {
        if (input.isEmpty()) return ByteArray(0)
        
        // Count leading '1's
        var leadingOnes = 0
        while (leadingOnes < input.length && input[leadingOnes] == '1') {
            leadingOnes++
        }
        
        // Convert from base58
        val digits = mutableListOf<Int>()
        for (i in leadingOnes until input.length) {
            val c = input[i]
            var carry = BASE58_ALPHABET.indexOf(c)
            if (carry < 0) throw IllegalArgumentException("Invalid Base58 character: $c")
            
            for (j in digits.indices.reversed()) {
                carry += digits[j] * 58
                digits[j] = carry and 0xff
                carry = carry shr 8
            }
            
            while (carry > 0) {
                digits.add(0, carry and 0xff)
                carry = carry shr 8
            }
        }
        
        // Skip leading zeros in digits (but keep the ones from input)
        var startAt = 0
        while (startAt < digits.size && digits[startAt] == 0 && startAt + leadingOnes < digits.size + leadingOnes) {
            startAt++
        }
        
        // Add leading zeros from input
        return ByteArray(leadingOnes) + digits.subList(startAt, digits.size).map { it.toByte() }.toByteArray()
    }
}
