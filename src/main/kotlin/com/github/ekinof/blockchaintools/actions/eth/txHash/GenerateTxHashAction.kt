package com.github.ekinof.blockchaintools.actions.eth.txHash

import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class GenerateTxHashAction(
    private val settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val state = settings.state
        var hash = EthAddressUtil.generateTxHash()
        if (!state.generateAddressInclude0x) {
            hash = hash.removePrefix("0x")
        }
        val output = when (state.generateAddressQuoteStyle) {
            BlockchainToolsSettings.QuoteStyle.SINGLE -> "'$hash'"
            BlockchainToolsSettings.QuoteStyle.DOUBLE -> "\"$hash\""
            BlockchainToolsSettings.QuoteStyle.NONE -> hash
        }
        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.insertString(editor.caretModel.offset, output)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
