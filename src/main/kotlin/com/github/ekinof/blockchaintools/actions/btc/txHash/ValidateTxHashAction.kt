package com.github.ekinof.blockchaintools.actions.btc.txHash

import com.github.ekinof.blockchaintools.actions.AbstractValidateAction
import com.github.ekinof.blockchaintools.util.BtcAddressUtil

class ValidateTxHashAction : AbstractValidateAction() {
    override fun validate(value: String): Boolean = BtcAddressUtil.isValidTxHash(value)
    override fun getNoSelectionMessageKey(): String = "action.error.no_btc_txhash_selection"
    override fun getInvalidValueMessageKey(): String = "action.error.invalid_btc_txhash"
    override fun getValidMessageKey(): String = "action.btc_txhash.valid"
}
