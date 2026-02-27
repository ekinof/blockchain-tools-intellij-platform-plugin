# Blockchain Address Tool — Design

**Date:** 2026-02-25
**Status:** Approved

## Summary

An IntelliJ Platform Plugin with a single configurable keyboard shortcut (`Alt+B` by default) that opens a popup menu with three blockchain address utilities:

1. **Generate Address** — inserts a random fake ERC20/Ethereum address at cursor
2. **Checksum Address** — validates EIP-55 checksum of the selected address, shows balloon notification
3. **Toggle Case** — toggles the selected address between lowercase and EIP-55 checksummed form

## Architecture

### Package layout

```
com.github.ekinof.blockchaintools
  actions/
    BlockchainActionsGroup.kt      ← ActionGroup containing the 3 actions
    GenerateAddressAction.kt       ← inserts random 0x... at cursor
    ChecksumAddressAction.kt       ← validates EIP-55, shows balloon notification
    ToggleCaseAddressAction.kt     ← toggles selection between lowercase ↔ EIP-55
  util/
    EthAddressUtil.kt              ← EIP-55 checksum logic using keccak256
```

### Trigger mechanism

An `ActionGroup` with `popup="true"` is registered in `plugin.xml`. The shortcut `Ctrl+Alt+B` opens this popup menu. All three actions are entries in the group. The shortcut is remappable via IntelliJ Keymap settings.

## Core Logic

### EthAddressUtil (EIP-55)

```
fun toChecksumAddress(address: String): String
  1. Strip "0x", lowercase the 40 hex chars
  2. Compute keccak256 of the ASCII string (not bytes)
  3. For each char: if letter AND hash nibble >= 8 → uppercase, else lowercase
  4. Re-attach "0x"

fun isValidChecksum(address: String): Boolean
  → address == toChecksumAddress(address)
```

**keccak256 implementation:** via Bouncy Castle (`org.bouncycastle:bcprov-jdk15on`) added as a Gradle dependency.

### GenerateAddressAction

- `SecureRandom().nextBytes(20)` → hex-encode → prepend `0x` → apply EIP-55 checksum
- Insert result at caret via `WriteCommandAction.runWriteCommandAction`
- No selection required; works at any cursor position

### ChecksumAddressAction

- Read selected text from editor
- Validate against regex `^0x[0-9a-fA-F]{40}$`
- If invalid format: show error balloon "Not a valid Ethereum address"
- If valid format: compare with `toChecksumAddress(input)`
  - Match → green balloon "✓ Valid EIP-55 checksum"
  - No match → red/warning balloon "✗ Invalid checksum"
- Input source: editor selection

### ToggleCaseAddressAction

- Read selected text from editor
- Validate against `^0x[0-9a-fA-F]{40}$`
- If already equals `toChecksumAddress(input)` → convert to lowercase (i.e. `0x` + 40 lowercase hex)
- Otherwise → apply `toChecksumAddress`
- Replace the selection in the editor via `WriteCommandAction`

## Plugin Registration (plugin.xml)

```xml
<actions>
  <group id="BlockchainActionsGroup" popup="true" text="Blockchain Tools">
    <action id="GenerateAddress"   class="...GenerateAddressAction"   text="Generate ERC20 Address"/>
    <action id="ChecksumAddress"   class="...ChecksumAddressAction"   text="Checksum Address"/>
    <action id="ToggleCaseAddress" class="...ToggleCaseAddressAction" text="Toggle Address Case"/>
    <keyboard-shortcut keymap="$default" first-keystroke="ctrl alt B"/>
  </group>
</actions>
<extensions defaultExtensionNs="com.intellij">
  <notificationGroup id="BlockchainTools" displayType="BALLOON"/>
</extensions>
```

## Dependencies

- Bouncy Castle (`org.bouncycastle:bcprov-jdk15on`) — keccak256 for EIP-55

## Error Handling

- No selection when checksum/toggleCase is triggered → show error balloon "Select an Ethereum address first"
- Selected text doesn't match address format → show error balloon "Not a valid Ethereum address"
- Actions are disabled (`update()` returns `presentation.isEnabled = false`) when no editor is open
