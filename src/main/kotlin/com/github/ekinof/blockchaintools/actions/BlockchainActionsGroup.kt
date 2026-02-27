package com.github.ekinof.blockchaintools.actions

import com.intellij.openapi.actionSystem.*

class BlockchainActionsGroup : DefaultActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val manager = ActionManager.getInstance()
        val result = mutableListOf<AnAction>()
        var counter = 1

        result.add(Separator("EIP-55 Address"))
        for (id in ADDRESS_ACTIONS) {
            val action = manager.getAction(id) ?: continue
            val text = "${counter++}. ${action.templatePresentation.text}"
            result.add(wrapWithText(action, text))
        }

        result.add(Separator("ETH TxHash"))
        for (id in TXHASH_ACTIONS) {
            val action = manager.getAction(id) ?: continue
            val text = "${counter++}. ${action.templatePresentation.text}"
            result.add(wrapWithText(action, text))
        }

        return result.toTypedArray()
    }

    private fun wrapWithText(action: AnAction, text: String): AnAction =
        object : AnAction() {
            init {
                templatePresentation.setText(text, false)
                copyShortcutFrom(action)
            }
            override fun actionPerformed(e: AnActionEvent) = action.actionPerformed(e)
            override fun update(e: AnActionEvent) {
                action.update(e)
                e.presentation.setText(text, false)
            }
        }

    companion object {
        val ADDRESS_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )
        val TXHASH_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateTxHashAction"
        )
    }
}
