package com.github.ekinof.blockchaintools.actions.btc.address

import com.github.ekinof.blockchaintools.actions.AbstractGenerateAction
import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.github.ekinof.blockchaintools.util.BtcAddressUtil

class GenerateAddressAction(
    settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : AbstractGenerateAction(settings) {
    override fun generateValue(): String = BtcAddressUtil.generateAddress()
}
