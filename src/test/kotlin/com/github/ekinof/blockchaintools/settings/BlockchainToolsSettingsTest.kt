package com.github.ekinof.blockchaintools.settings

import junit.framework.TestCase

class BlockchainToolsSettingsTest : TestCase() {

    fun testDefaultStateHasNoneQuoteStyleAndAllBlockchainsEnabled() {
        val settings = BlockchainToolsSettings()
        assertEquals(BlockchainToolsSettings.QuoteStyle.NONE, settings.state.quoteStyle)
        assertTrue(settings.state.ethEnabled)
        assertTrue(settings.state.btcEnabled)
        assertTrue(settings.state.solEnabled)
        assertTrue(settings.state.ethInclude0x)
    }

    fun testLoadStateRestoresCustomValues() {
        val settings = BlockchainToolsSettings()
        val newState = BlockchainToolsSettings.State(
            quoteStyle = BlockchainToolsSettings.QuoteStyle.DOUBLE,
            ethEnabled = false,
            btcEnabled = true,
            solEnabled = false,
            ethInclude0x = false
        )
        settings.loadState(newState)
        assertEquals(BlockchainToolsSettings.QuoteStyle.DOUBLE, settings.state.quoteStyle)
        assertFalse(settings.state.ethEnabled)
        assertTrue(settings.state.btcEnabled)
        assertFalse(settings.state.solEnabled)
        assertFalse(settings.state.ethInclude0x)
    }

    fun testGetStateRoundTrip() {
        val settings = BlockchainToolsSettings()
        settings.loadState(BlockchainToolsSettings.State(
            quoteStyle = BlockchainToolsSettings.QuoteStyle.SINGLE,
            ethEnabled = true,
            btcEnabled = false,
            solEnabled = true,
            ethInclude0x = true
        ))
        val state = settings.state
        val settings2 = BlockchainToolsSettings()
        settings2.loadState(state)
        assertEquals(state, settings2.state)
    }
}
