package com.github.ekinof.blockchaintools.settings

import com.intellij.openapi.components.*

@Service(Service.Level.APP)
@State(
    name = "BlockchainToolsSettings",
    storages = [Storage("blockchain-tools.xml")]
)
class BlockchainToolsSettings : PersistentStateComponent<BlockchainToolsSettings.State> {

    data class State(
        var actionOrder: MutableList<String> = DEFAULT_ORDER.toMutableList()
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        val DEFAULT_ORDER = listOf(
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )

        fun getInstance(): BlockchainToolsSettings = service()
    }
}
