package com.github.ekinof.blockchaintools.actions.eth.address

import com.github.ekinof.blockchaintools.actions.eth.applyGenerateSettings
import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class GenerateAddressAction(
    private val settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val output = applyGenerateSettings(EthAddressUtil.generateAddress(), settings.state)
        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.insertString(editor.caretModel.offset, output)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
