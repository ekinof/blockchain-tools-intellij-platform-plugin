package com.github.ekinof.blockchaintools.settings

import junit.framework.TestCase

class BlockchainToolsSettingsTest : TestCase() {

    fun testDefaultOrderContainsAllThreeActions() {
        val settings = BlockchainToolsSettings()
        assertEquals(3, settings.state.actionOrder.size)
        assertTrue(settings.state.actionOrder.contains("com.github.ekinof.blockchaintools.GenerateAddressAction"))
        assertTrue(settings.state.actionOrder.contains("com.github.ekinof.blockchaintools.ChecksumAddressAction"))
        assertTrue(settings.state.actionOrder.contains("com.github.ekinof.blockchaintools.ToggleCaseAddressAction"))
    }

    fun testLoadStateRestoresCustomOrder() {
        val settings = BlockchainToolsSettings()
        val newOrder = mutableListOf(
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )
        settings.loadState(BlockchainToolsSettings.State(newOrder))
        assertEquals(newOrder, settings.state.actionOrder)
    }

    fun testGetStateRoundTrip() {
        val settings = BlockchainToolsSettings()
        val state = settings.state
        val settings2 = BlockchainToolsSettings()
        settings2.loadState(state)
        assertEquals(state.actionOrder, settings2.state.actionOrder)
    }
}
