package com.github.ekinof.blockchaintools.actions.eth.address

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.github.ekinof.blockchaintools.actions.notifyBalloon
import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class ChecksumAddressAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText
        if (selectedText == null) {
            notifyBalloon(e.project, BlockchainToolsBundle.message("action.error.no_selection"), NotificationType.WARNING)
            return
        }
        if (!EthAddressUtil.isValidAddress(selectedText)) {
            notifyBalloon(e.project, BlockchainToolsBundle.message("action.error.invalid_address"), NotificationType.ERROR)
            return
        }
        if (EthAddressUtil.isValidChecksum(selectedText)) {
            notifyBalloon(e.project, BlockchainToolsBundle.message("action.checksum.valid"), NotificationType.INFORMATION)
        } else {
            notifyBalloon(e.project, BlockchainToolsBundle.message("action.checksum.invalid"), NotificationType.WARNING)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
