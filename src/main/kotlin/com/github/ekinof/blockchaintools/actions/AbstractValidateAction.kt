package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

abstract class AbstractValidateAction : AnAction() {

    abstract fun validate(value: String): Boolean
    abstract fun getNoSelectionMessageKey(): String
    abstract fun getInvalidValueMessageKey(): String
    abstract fun getValidMessageKey(): String

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText
        if (selectedText == null) {
            notifyBalloon(e.project, BlockchainToolsBundle.message(getNoSelectionMessageKey()), NotificationType.WARNING)
            return
        }
        if (!validate(selectedText)) {
            notifyBalloon(e.project, BlockchainToolsBundle.message(getInvalidValueMessageKey()), NotificationType.ERROR)
            return
        }
        notifyBalloon(e.project, BlockchainToolsBundle.message(getValidMessageKey()), NotificationType.INFORMATION)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
