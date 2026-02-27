# Sections and ETH TxHash Design

**Date:** 2026-02-27

## Goal

Reorganise the Blockchain Tools popup into two named sections and add a new Generate TxHash action.

## Popup Structure

Flat list with IntelliJ `Separator` labels between sections:

```
Blockchain Tools
─── EIP-55 Address ───
1. Generate ERC20 Address
2. Checksum Address
3. Toggle Address Case
─── ETH TxHash ───
4. Generate TxHash
```

## New Action: GenerateTxHashAction

Generates a random 32-byte value using `SecureRandom` (32 bytes → 64 lowercase hex chars).
Applies the same user settings as `GenerateAddressAction`:
- `generateAddressQuoteStyle` (NONE / SINGLE / DOUBLE)
- `generateAddressInclude0x` (true by default)

## Changes

| File | Change |
|------|--------|
| `EthAddressUtil` | Add `generateTxHash()` |
| `GenerateTxHashAction` | New action, mirrors `GenerateAddressAction` |
| `BlockchainActionsGroup` | Insert `Separator("EIP-55 Address")` and `Separator("ETH TxHash")` with continuous numbering |
| `plugin.xml` | Register new action in the group |
| Tests | `EthAddressUtilTest` + `BlockchainActionsTest` coverage for new action |

## Settings

No new settings. Existing quote style and 0x prefix settings apply to both generators.
