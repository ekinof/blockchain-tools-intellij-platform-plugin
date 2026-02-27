package com.github.ekinof.blockchaintools.actions

import com.intellij.openapi.actionSystem.Separator
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class BlockchainActionsGroupTest : BasePlatformTestCase() {

    fun testGroupHasSixSeparatorsAndThirteenActions() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        // 6 Separators + 13 actions (3 ETH addr + 2 ETH tx + 2 BTC addr + 2 BTC tx + 2 SOL addr + 2 SOL sig) = 19 total
        assertEquals(19, children.size)
    }

    fun testFirstChildIsEthAddressSeparator() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[0] is Separator)
        assertEquals("ETH Address", (children[0] as Separator).text)
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

    fun testBtcAddressSeparatorExists() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[7] is Separator)
        assertEquals("BTC Address", (children[7] as Separator).text)
    }

    fun testBtcActionsAreNumbered6To9() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[8].templatePresentation.text.startsWith("6."))
        assertTrue(children[8].templatePresentation.text.contains("BTC"))
        assertTrue(children[9].templatePresentation.text.startsWith("7."))
        assertTrue(children[9].templatePresentation.text.contains("Validate"))
    }

    fun testSolAddressSeparatorExists() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[13] is Separator)
        assertEquals("SOL Address", (children[13] as Separator).text)
    }

    fun testSolActionsAreNumbered() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[14].templatePresentation.text.startsWith("10."))
        assertTrue(children[14].templatePresentation.text.contains("SOL"))
        assertTrue(children[15].templatePresentation.text.startsWith("11."))
        assertTrue(children[15].templatePresentation.text.contains("Validate"))
    }
}
