package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.intellij.openapi.actionSystem.*

class BlockchainActionsGroup(
    private val settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : DefaultActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val manager = ActionManager.getInstance()
        return settings.state.actionOrder.mapIndexedNotNull { index, id ->
            val action = manager.getAction(id) ?: return@mapIndexedNotNull null
            val label = manager.getAction(id)?.templatePresentation?.text ?: id
            val text = "${index + 1}. $label"
            object : AnAction() {
                init {
                    templatePresentation.setText(text, false)
                    copyShortcutFrom(action)
                }
                override fun actionPerformed(e: AnActionEvent) = action.actionPerformed(e)
                override fun update(e: AnActionEvent) {
                    // Delegate first so isEnabled/isVisible are applied, then re-stamp
                    // the mnemonic text last so the delegate cannot overwrite it.
                    action.update(e)
                    e.presentation.setText(text, false)
                }
            }
        }.toTypedArray()
    }
}
