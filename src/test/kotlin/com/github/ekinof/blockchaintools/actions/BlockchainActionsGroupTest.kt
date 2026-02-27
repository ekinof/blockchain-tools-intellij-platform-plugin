package com.github.ekinof.blockchaintools.actions

import com.intellij.openapi.actionSystem.Separator
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class BlockchainActionsGroupTest : BasePlatformTestCase() {

    fun testGroupHasTwoSeparatorsAndFiveActions() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        // Separator + 3 address actions + Separator + 2 txhash actions = 7 total
        assertEquals(7, children.size)
    }

    fun testFirstChildIsEip55Separator() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[0] is Separator)
        assertEquals("EIP-55 Address", (children[0] as Separator).text)
    }

    fun testAddressActionsAreNumbered1To3() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[1].templatePresentation.text.startsWith("1."))
        assertTrue(children[1].templatePresentation.text.contains("Generate"))
        assertTrue(children[2].templatePresentation.text.startsWith("2."))
        assertTrue(children[2].templatePresentation.text.contains("Checksum"))
        assertTrue(children[3].templatePresentation.text.startsWith("3."))
        assertTrue(children[3].templatePresentation.text.contains("Toggle"))
    }

    fun testFifthChildIsEthTxHashSeparator() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[4] is Separator)
        assertEquals("ETH TxHash", (children[4] as Separator).text)
    }

    fun testTxHashActionsAreNumbered4And5() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[5].templatePresentation.text.startsWith("4."))
        assertTrue(children[5].templatePresentation.text.contains("TxHash"))
        assertTrue(children[6].templatePresentation.text.startsWith("5."))
        assertTrue(children[6].templatePresentation.text.contains("Validate"))
    }
}
