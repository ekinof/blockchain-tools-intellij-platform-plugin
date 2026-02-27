package com.github.ekinof.blockchaintools.settings

import com.intellij.openapi.components.*

@Service(Service.Level.APP)
@State(
    name = "BlockchainToolsSettings",
    storages = [Storage("blockchain-tools.xml")]
)
class BlockchainToolsSettings : PersistentStateComponent<BlockchainToolsSettings.State> {

    enum class QuoteStyle { NONE, SINGLE, DOUBLE }

    data class State(
        // Global quote style for all blockchains
        var quoteStyle: QuoteStyle = QuoteStyle.NONE,
        
        // Enabled blockchains
        var ethEnabled: Boolean = true,
        var btcEnabled: Boolean = true,
        var solEnabled: Boolean = true,
        
        // ETH-specific settings
        var ethInclude0x: Boolean = true
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(): BlockchainToolsSettings = service()
    }
}
