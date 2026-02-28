<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Blockchain Tools Changelog

## [Unreleased]

## [0.0.2] - 2026-02-28
### Added
- "Blockchain Tools Settings" shortcut at the end of the editor right-click context menu
- "Blockchain Tools Settings" entry at the bottom of the Blockchain Tools popup (Ctrl+Alt+Shift+B)

### Fixed
- Qodana: `BECH32_ALPHABET` promoted to `const val` in `BtcAddressUtil`
- Qodana: simplified redundant `[1]` character class to `1` in P2PKH regex

## [0.0.1] - 2026-02-27
### Added
- Ethereum (ERC20) address generation with EIP-55 checksumming
- Ethereum address validation and case toggling
- Ethereum transaction hash generation and validation
- Bitcoin address generation (P2PKH, P2SH, Bech32) and validation
- Bitcoin transaction hash generation and validation
- Solana address generation and validation (Base58)
- Solana transaction signature generation and validation
- Quick action menu accessible via `Ctrl+Alt+Shift+B`
- Keyboard shortcuts for all actions (numeric keys 1-9 and Ctrl+1-4)
- Settings panel to customize quote styles and enable/disable blockchains
- Comprehensive unit tests with cryptographic test vectors

[Unreleased]: https://github.com/ekinof/blockchain-tools-intellij-platform-plugin/compare/0.0.2...HEAD
[0.0.2]: https://github.com/ekinof/blockchain-tools-intellij-platform-plugin/compare/0.0.1...0.0.2
[0.0.1]: https://github.com/ekinof/blockchain-tools-intellij-platform-plugin/commits/0.0.1
