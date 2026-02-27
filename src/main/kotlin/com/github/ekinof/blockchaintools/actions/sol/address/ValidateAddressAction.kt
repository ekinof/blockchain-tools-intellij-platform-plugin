package com.github.ekinof.blockchaintools.actions.sol.address

import com.github.ekinof.blockchaintools.actions.AbstractValidateAction
import com.github.ekinof.blockchaintools.util.SolAddressUtil

class ValidateAddressAction : AbstractValidateAction() {
    override fun validate(value: String): Boolean = SolAddressUtil.isValidAddress(value)
    override fun getNoSelectionMessageKey(): String = "action.error.no_sol_selection"
    override fun getInvalidValueMessageKey(): String = "action.error.invalid_sol_address"
    override fun getValidMessageKey(): String = "action.sol_address.valid"
}
