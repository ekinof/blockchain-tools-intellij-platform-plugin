package com.github.ekinof.blockchaintools.actions.eth.address

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.github.ekinof.blockchaintools.actions.eth.notifyBalloon
import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class ToggleCaseAddressAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText
        if (selectedText == null) {
            notifyBalloon(e.project, BlockchainToolsBundle.message("action.error.no_selection"), NotificationType.WARNING)
            return
        }
        if (!EthAddressUtil.isValidAddress(selectedText)) {
            notifyBalloon(e.project, BlockchainToolsBundle.message("action.error.invalid_address"), NotificationType.ERROR)
            return
        }
        val toggled = EthAddressUtil.toggleCase(selectedText)
        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.replaceString(
                selectionModel.selectionStart,
                selectionModel.selectionEnd,
                toggled
            )
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
