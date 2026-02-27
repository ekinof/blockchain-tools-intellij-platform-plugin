package com.github.ekinof.blockchaintools.actions.sol.signature

import com.github.ekinof.blockchaintools.actions.AbstractValidateAction
import com.github.ekinof.blockchaintools.util.SolAddressUtil

class ValidateSignatureAction : AbstractValidateAction() {
    override fun validate(value: String): Boolean = SolAddressUtil.isValidSignature(value)
    override fun getNoSelectionMessageKey(): String = "action.error.no_sol_signature_selection"
    override fun getInvalidValueMessageKey(): String = "action.error.invalid_sol_signature"
    override fun getValidMessageKey(): String = "action.sol_signature.valid"
}
