package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.intellij.openapi.actionSystem.*

class BlockchainActionsGroup : DefaultActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val manager = ActionManager.getInstance()
        val result = mutableListOf<AnAction>()
        val settings = BlockchainToolsSettings.getInstance().state
        var counter = 1

        // ETH Section
        if (settings.ethEnabled) {
            result.add(Separator("ETH Address"))
            for (id in ETH_ADDRESS_ACTIONS) {
                val action = manager.getAction(id) ?: continue
                val text = "${counter++}. ${action.templatePresentation.text}"
                result.add(wrapWithText(action, text))
            }

            result.add(Separator("ETH TxHash"))
            for (id in ETH_TXHASH_ACTIONS) {
                val action = manager.getAction(id) ?: continue
                val text = "${counter++}. ${action.templatePresentation.text}"
                result.add(wrapWithText(action, text))
            }
        }

        // BTC Section
        if (settings.btcEnabled) {
            result.add(Separator("BTC Address"))
            for (id in BTC_ADDRESS_ACTIONS) {
                val action = manager.getAction(id) ?: continue
                val text = "${counter++}. ${action.templatePresentation.text}"
                result.add(wrapWithText(action, text))
            }

            result.add(Separator("BTC TxHash"))
            for (id in BTC_TXHASH_ACTIONS) {
                val action = manager.getAction(id) ?: continue
                val text = "${counter++}. ${action.templatePresentation.text}"
                result.add(wrapWithText(action, text))
            }
        }

        // SOL Section
        if (settings.solEnabled) {
            result.add(Separator("SOL Address"))
            for (id in SOL_ADDRESS_ACTIONS) {
                val action = manager.getAction(id) ?: continue
                val text = "${counter++}. ${action.templatePresentation.text}"
                result.add(wrapWithText(action, text))
            }

            result.add(Separator("SOL Signature"))
            for (id in SOL_SIGNATURE_ACTIONS) {
                val action = manager.getAction(id) ?: continue
                val text = "${counter++}. ${action.templatePresentation.text}"
                result.add(wrapWithText(action, text))
            }
        }

        if (result.isNotEmpty()) {
            result.add(Separator())
            val settingsAction = manager.getAction("com.github.ekinof.blockchaintools.OpenSettingsAction")
            if (settingsAction != null) result.add(settingsAction)
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
        private val ETH_ADDRESS_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )
        private val ETH_TXHASH_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateTxHashAction",
            "com.github.ekinof.blockchaintools.ValidateTxHashAction"
        )
        private val BTC_ADDRESS_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateBtcAddressAction",
            "com.github.ekinof.blockchaintools.ValidateBtcAddressAction"
        )
        private val BTC_TXHASH_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateBtcTxHashAction",
            "com.github.ekinof.blockchaintools.ValidateBtcTxHashAction"
        )
        private val SOL_ADDRESS_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateSolAddressAction",
            "com.github.ekinof.blockchaintools.ValidateSolAddressAction"
        )
        private val SOL_SIGNATURE_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateSolSignatureAction",
            "com.github.ekinof.blockchaintools.ValidateSolSignatureAction"
        )
    }
}
