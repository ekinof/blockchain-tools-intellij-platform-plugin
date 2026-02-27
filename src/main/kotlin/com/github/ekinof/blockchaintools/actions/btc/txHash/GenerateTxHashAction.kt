package com.github.ekinof.blockchaintools.actions.btc.txHash

import com.github.ekinof.blockchaintools.actions.AbstractGenerateAction
import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.github.ekinof.blockchaintools.util.BtcAddressUtil

class GenerateTxHashAction(
    settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : AbstractGenerateAction(settings) {
    override fun generateValue(): String = BtcAddressUtil.generateTxHash()
}
