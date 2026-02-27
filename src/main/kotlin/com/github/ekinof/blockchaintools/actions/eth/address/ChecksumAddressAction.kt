package com.github.ekinof.blockchaintools.actions.eth.address

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project

class ChecksumAddressAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText
        if (selectedText == null) {
            notify(e.project, BlockchainToolsBundle.message("action.error.no_selection"), NotificationType.WARNING)
            return
        }
        if (!EthAddressUtil.isValidAddress(selectedText)) {
            notify(e.project, BlockchainToolsBundle.message("action.error.invalid_address"), NotificationType.ERROR)
            return
        }
        if (EthAddressUtil.isValidChecksum(selectedText)) {
            notify(e.project, BlockchainToolsBundle.message("action.checksum.valid"), NotificationType.INFORMATION)
        } else {
            notify(e.project, BlockchainToolsBundle.message("action.checksum.invalid"), NotificationType.WARNING)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }

    private fun notify(project: Project?, message: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("BlockchainTools")
            .createNotification(message, type)
            .notify(project)
    }
}
