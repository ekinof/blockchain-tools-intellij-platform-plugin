package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.testFramework.MapDataContext
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class BlockchainActionsTest : BasePlatformTestCase() {

    private fun makeEvent(action: com.intellij.openapi.actionSystem.AnAction): AnActionEvent {
        val context = MapDataContext()
        context.put(CommonDataKeys.PROJECT, project)
        context.put(CommonDataKeys.EDITOR, myFixture.editor)
        return AnActionEvent.createFromDataContext("test", Presentation(), context)
    }

    fun testGenerateAddressInsertsValidChecksummedAddress() {
        myFixture.configureByText(PlainTextFileType.INSTANCE, "")
        val action = GenerateAddressAction()
        action.actionPerformed(makeEvent(action))
        val text = myFixture.editor.document.text
        assertTrue("Expected valid address, got: $text", EthAddressUtil.isValidAddress(text))
        assertTrue("Expected checksummed address, got: $text", EthAddressUtil.isValidChecksum(text))
    }

    fun testChecksumActionDoesNotModifyDocumentForValidAddress() {
        val validChecksummed = "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed"
        myFixture.configureByText(PlainTextFileType.INSTANCE, validChecksummed)
        myFixture.editor.selectionModel.setSelection(0, validChecksummed.length)
        val action = ChecksumAddressAction()
        action.actionPerformed(makeEvent(action))
        assertEquals(validChecksummed, myFixture.editor.document.text)
    }

    fun testChecksumActionDoesNotModifyDocumentForInvalidAddress() {
        val lowercase = "0x5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"
        myFixture.configureByText(PlainTextFileType.INSTANCE, lowercase)
        myFixture.editor.selectionModel.setSelection(0, lowercase.length)
        val action = ChecksumAddressAction()
        action.actionPerformed(makeEvent(action))
        assertEquals(lowercase, myFixture.editor.document.text)
    }

    fun testToggleCaseConvertsChecksummedToLowercase() {
        val checksummed = "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed"
        val expected = "0x5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"
        myFixture.configureByText(PlainTextFileType.INSTANCE, checksummed)
        myFixture.editor.selectionModel.setSelection(0, checksummed.length)
        val action = ToggleCaseAddressAction()
        action.actionPerformed(makeEvent(action))
        assertEquals(expected, myFixture.editor.document.text)
    }

    fun testToggleCaseConvertsLowercaseToChecksummed() {
        val lowercase = "0x5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"
        val expected = "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed"
        myFixture.configureByText(PlainTextFileType.INSTANCE, lowercase)
        myFixture.editor.selectionModel.setSelection(0, lowercase.length)
        val action = ToggleCaseAddressAction()
        action.actionPerformed(makeEvent(action))
        assertEquals(expected, myFixture.editor.document.text)
    }

    fun testToggleCaseDoesNothingWithNoSelection() {
        val text = "no address here"
        myFixture.configureByText(PlainTextFileType.INSTANCE, text)
        val action = ToggleCaseAddressAction()
        action.actionPerformed(makeEvent(action))
        assertEquals(text, myFixture.editor.document.text)
    }
}
