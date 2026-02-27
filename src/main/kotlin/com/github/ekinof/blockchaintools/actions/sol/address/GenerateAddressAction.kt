package com.github.ekinof.blockchaintools.actions.sol.address

import com.github.ekinof.blockchaintools.actions.AbstractGenerateAction
import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.github.ekinof.blockchaintools.util.SolAddressUtil

class GenerateAddressAction(
    settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : AbstractGenerateAction(settings) {
    override fun generateValue(): String = SolAddressUtil.generateAddress()
}
