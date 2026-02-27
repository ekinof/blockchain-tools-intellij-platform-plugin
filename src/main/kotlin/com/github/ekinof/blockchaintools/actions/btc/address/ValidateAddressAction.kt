package com.github.ekinof.blockchaintools.actions.btc.address

import com.github.ekinof.blockchaintools.actions.AbstractValidateAction
import com.github.ekinof.blockchaintools.util.BtcAddressUtil

class ValidateAddressAction : AbstractValidateAction() {
    override fun validate(value: String): Boolean = BtcAddressUtil.isValidAddress(value)
    override fun getNoSelectionMessageKey(): String = "action.error.no_btc_selection"
    override fun getInvalidValueMessageKey(): String = "action.error.invalid_btc_address"
    override fun getValidMessageKey(): String = "action.btc_address.valid"
}
