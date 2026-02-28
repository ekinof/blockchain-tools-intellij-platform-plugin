# Blockchain Tools

![Build](https://github.com/ekinof/blockchain-tools-intellij-platform-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/30393.svg)](https://plugins.jetbrains.com/plugin/30393)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/30393.svg)](https://plugins.jetbrains.com/plugin/30393)

<!-- Plugin description -->
**Blockchain Tools** is an IntelliJ Platform plugin that helps developers working with blockchain applications by providing quick generation and validation of blockchain-related data.

## Features

### Ethereum (ERC20)
- **Generate Address**: Insert a random checksummed Ethereum address
- **Checksum Address**: Verify EIP-55 checksum of selected addresses
- **Toggle Address Case**: Convert between lowercase and checksummed formats
- **Generate TxHash**: Insert a random Ethereum transaction hash (0x + 64 hex chars)
- **Validate TxHash**: Verify transaction hash format

### Bitcoin
- **Generate Address**: Create random Bitcoin addresses (P2PKH, P2SH, Bech32)
- **Validate Address**: Check Bitcoin address validity and format
- **Generate TxHash**: Insert random Bitcoin transaction hashes
- **Validate TxHash**: Verify Bitcoin transaction hash format

### Solana
- **Generate Address**: Create random Solana addresses (Base58-encoded)
- **Validate Address**: Verify Solana address format
- **Generate Signature**: Insert random Solana transaction signatures
- **Validate Signature**: Check Solana signature validity

## Usage

Access all tools via:
- **Keyboard**: `Ctrl+Alt+Shift+B` to open the action menu
- **Actions Menu**: Search for "Blockchain Tools" in the action popup
- **Direct Shortcuts**: Numeric keys 1-9 and Ctrl+1-4 for specific actions

All generated data is cryptographically random and suitable for testing purposes.

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "blockchain-tools"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/30393) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/30393/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/ekinof/blockchain-tools/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
