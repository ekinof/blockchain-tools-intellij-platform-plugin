package com.github.ekinof.blockchaintools.settings

import junit.framework.TestCase

class BlockchainToolsSettingsTest : TestCase() {

    fun testDefaultStateHasNoneQuoteStyleAndInclude0x() {
        val settings = BlockchainToolsSettings()
        assertEquals(BlockchainToolsSettings.QuoteStyle.NONE, settings.state.generateAddressQuoteStyle)
        assertTrue(settings.state.generateAddressInclude0x)
    }

    fun testLoadStateRestoresCustomValues() {
        val settings = BlockchainToolsSettings()
        val newState = BlockchainToolsSettings.State(
            generateAddressQuoteStyle = BlockchainToolsSettings.QuoteStyle.DOUBLE,
            generateAddressInclude0x = false
        )
        settings.loadState(newState)
        assertEquals(BlockchainToolsSettings.QuoteStyle.DOUBLE, settings.state.generateAddressQuoteStyle)
        assertFalse(settings.state.generateAddressInclude0x)
    }

    fun testGetStateRoundTrip() {
        val settings = BlockchainToolsSettings()
        settings.loadState(BlockchainToolsSettings.State(
            generateAddressQuoteStyle = BlockchainToolsSettings.QuoteStyle.SINGLE,
            generateAddressInclude0x = true
        ))
        val state = settings.state
        val settings2 = BlockchainToolsSettings()
        settings2.loadState(state)
        assertEquals(state, settings2.state)
    }
}
