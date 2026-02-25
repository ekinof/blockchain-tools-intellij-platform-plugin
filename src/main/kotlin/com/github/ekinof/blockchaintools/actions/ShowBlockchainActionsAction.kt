package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.ui.popup.PopupFactoryImpl

class ShowBlockchainActionsAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val group = ActionManager.getInstance()
            .getAction("com.github.ekinof.blockchaintools.BlockchainActionsGroup") as? ActionGroup ?: return
        val popup = PopupFactoryImpl.getInstance()
            .createActionGroupPopup(BlockchainToolsBundle.message("popup.title"), group, e.dataContext, null, true)
        popup.showInBestPositionFor(e.dataContext)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
