# Validate TxHash Design

**Date:** 2026-02-27

## Goal

Add a Validate TxHash action to the ETH TxHash section that checks whether a selected string is a valid Ethereum transaction hash and reports the result via a balloon notification.

## Behaviour

- **Valid:** selected text matches `0x` + exactly 64 hex characters → show ✓ notification (INFORMATION)
- **Invalid format:** selected text does not match → show ✗ notification (ERROR)
- **No selection:** → show warning notification (same pattern as address actions)
- No document modification (read-only action)

## Changes

| File | Change |
|------|--------|
| `EthAddressUtil` | Add `isValidTxHash(hash: String): Boolean` — regex `^0x[0-9a-fA-F]{64}$` |
| `ValidateTxHashAction` | New action at `actions/eth/txHash/`, mirrors `ChecksumAddressAction` |
| `plugin.xml` | Register action in the group |
| `BlockchainActionsGroup` | Add to `TXHASH_ACTIONS` (numbered 5) |
| `BlockchainToolsBundle.properties` | Add `action.txhash.valid`, `action.txhash.invalid` |
| Tests | `EthAddressUtilTest` + `BlockchainActionsTest` |

## No ToggleCase

Transaction hashes have no EIP-55 equivalent. Toggling case carries no semantic meaning, so no ToggleCase action is added.
