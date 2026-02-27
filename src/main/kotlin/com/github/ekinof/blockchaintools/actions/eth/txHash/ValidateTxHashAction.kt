package com.github.ekinof.blockchaintools.actions.eth.txHash

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.github.ekinof.blockchaintools.actions.notifyBalloon
import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class ValidateTxHashAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText
        if (selectedText == null) {
            notifyBalloon(e.project, BlockchainToolsBundle.message("action.error.no_txhash_selection"), NotificationType.WARNING)
            return
        }
        if (!EthAddressUtil.isValidTxHash(selectedText)) {
            notifyBalloon(e.project, BlockchainToolsBundle.message("action.error.invalid_txhash"), NotificationType.ERROR)
            return
        }
        notifyBalloon(e.project, BlockchainToolsBundle.message("action.txhash.valid"), NotificationType.INFORMATION)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
