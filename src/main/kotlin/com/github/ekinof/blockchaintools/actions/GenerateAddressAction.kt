package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class GenerateAddressAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val address = EthAddressUtil.generateAddress()
        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.insertString(editor.caretModel.offset, address)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
