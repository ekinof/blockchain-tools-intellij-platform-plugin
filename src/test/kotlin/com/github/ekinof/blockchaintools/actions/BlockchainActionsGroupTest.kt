package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class BlockchainActionsGroupTest : BasePlatformTestCase() {

    fun testDefaultOrderProducesThreeChildren() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertEquals(3, children.size)
        assertTrue(children[0].templatePresentation.text.contains("Generate"))
        assertTrue(children[1].templatePresentation.text.contains("Checksum"))
        assertTrue(children[2].templatePresentation.text.contains("Toggle"))
    }

    fun testChildrenHavePositionBasedMnemonics() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[0].templatePresentation.text.startsWith("1."))
        assertTrue(children[1].templatePresentation.text.startsWith("2."))
        assertTrue(children[2].templatePresentation.text.startsWith("3."))
    }

    fun testCustomOrderChangesChildSequence() {
        val settings = BlockchainToolsSettings()
        settings.loadState(BlockchainToolsSettings.State(mutableListOf(
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )))
        val group = BlockchainActionsGroup(settings)
        val children = group.getChildren(null)
        assertTrue(children[0].templatePresentation.text.contains("Checksum"))
        assertTrue(children[1].templatePresentation.text.contains("Generate"))
        assertTrue(children[2].templatePresentation.text.contains("Toggle"))
    }

    fun testEmptyOrderProducesNoChildren() {
        val settings = BlockchainToolsSettings()
        settings.loadState(BlockchainToolsSettings.State(mutableListOf()))
        val group = BlockchainActionsGroup(settings)
        assertEquals(0, group.getChildren(null).size)
    }
}
