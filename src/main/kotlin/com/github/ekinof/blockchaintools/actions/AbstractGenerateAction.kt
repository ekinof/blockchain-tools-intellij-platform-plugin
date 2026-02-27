package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.actions.eth.applyGenerateSettings
import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

abstract class AbstractGenerateAction(
    private val settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : AnAction() {

    abstract fun generateValue(): String

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val output = applyGenerateSettings(generateValue(), settings.state)
        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.insertString(editor.caretModel.offset, output)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
