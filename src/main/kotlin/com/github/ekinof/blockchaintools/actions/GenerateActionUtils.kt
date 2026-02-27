package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings

internal fun applyGenerateSettings(value: String, state: BlockchainToolsSettings.State): String {
    val withPrefix = if (state.ethInclude0x) value else value.removePrefix("0x")
    return when (state.quoteStyle) {
        BlockchainToolsSettings.QuoteStyle.SINGLE -> "'$withPrefix'"
        BlockchainToolsSettings.QuoteStyle.DOUBLE -> "\"$withPrefix\""
        BlockchainToolsSettings.QuoteStyle.NONE -> withPrefix
    }
}
