package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.settings.BlockchainToolsConfigurable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil

class OpenSettingsAction : AnAction("Blockchain Tools Settings") {

    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(
            e.project,
            BlockchainToolsConfigurable::class.java
        )
    }
}
