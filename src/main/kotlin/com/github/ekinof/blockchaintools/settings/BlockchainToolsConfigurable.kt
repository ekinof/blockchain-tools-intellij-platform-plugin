package com.github.ekinof.blockchaintools.settings

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.intellij.openapi.options.Configurable
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBLabel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class BlockchainToolsConfigurable : Configurable {

    // Quote Style (global)
    private val noneRadio = JRadioButton(BlockchainToolsBundle.message("settings.quote.none"))
    private val singleRadio = JRadioButton(BlockchainToolsBundle.message("settings.quote.single"))
    private val doubleRadio = JRadioButton(BlockchainToolsBundle.message("settings.quote.double"))
    private val quoteGroup = ButtonGroup()
    
    // Enabled Blockchains
    private val ethEnabledCheckbox = JCheckBox(BlockchainToolsBundle.message("settings.blockchain.eth"))
    private val btcEnabledCheckbox = JCheckBox(BlockchainToolsBundle.message("settings.blockchain.btc"))
    private val solEnabledCheckbox = JCheckBox(BlockchainToolsBundle.message("settings.blockchain.sol"))
    
    // ETH-specific settings
    private val ethInclude0xCheckbox = JCheckBox(BlockchainToolsBundle.message("settings.eth.include_0x"))

    override fun getDisplayName() = "Blockchain Tools"

    override fun createComponent(): JComponent {
        quoteGroup.add(noneRadio)
        quoteGroup.add(singleRadio)
        quoteGroup.add(doubleRadio)
        reset()

        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            anchor = GridBagConstraints.WEST
            insets = Insets(4, 0, 4, 0)
            gridx = 0
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }

        var row = 0
        
        // Section 1: Quote Style (global)
        gbc.gridy = row++
        panel.add(TitledSeparator(BlockchainToolsBundle.message("settings.section.quote_style")), gbc)
        
        val quotePanel = JPanel()
        quotePanel.layout = BoxLayout(quotePanel, BoxLayout.X_AXIS)
        quotePanel.add(JBLabel(BlockchainToolsBundle.message("settings.quote_label")))
        quotePanel.add(Box.createHorizontalStrut(8))
        quotePanel.add(noneRadio)
        quotePanel.add(singleRadio)
        quotePanel.add(doubleRadio)
        
        gbc.insets = Insets(4, 16, 4, 0)
        gbc.gridy = row++
        panel.add(quotePanel, gbc)
        
        // Section 2: Enabled Blockchains
        gbc.insets = Insets(16, 0, 4, 0)
        gbc.gridy = row++
        panel.add(TitledSeparator(BlockchainToolsBundle.message("settings.section.blockchains")), gbc)
        
        gbc.insets = Insets(4, 16, 4, 0)
        gbc.gridy = row++
        panel.add(ethEnabledCheckbox, gbc)
        gbc.gridy = row++
        panel.add(btcEnabledCheckbox, gbc)
        gbc.gridy = row++
        panel.add(solEnabledCheckbox, gbc)
        
        // Section 3: Blockchain-specific settings
        gbc.insets = Insets(16, 0, 4, 0)
        gbc.gridy = row++
        panel.add(TitledSeparator(BlockchainToolsBundle.message("settings.section.blockchain_specific")), gbc)
        
        gbc.insets = Insets(4, 16, 4, 0)
        gbc.gridy = row++
        panel.add(JBLabel(BlockchainToolsBundle.message("settings.eth.label")), gbc)
        gbc.insets = Insets(4, 32, 4, 0)
        gbc.gridy = row++
        panel.add(ethInclude0xCheckbox, gbc)
        
        // Spacer to push everything to the top
        gbc.insets = Insets(4, 0, 4, 0)
        gbc.gridy = row
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        panel.add(JPanel(), gbc)

        return panel
    }

    override fun isModified(): Boolean {
        val state = BlockchainToolsSettings.getInstance().state
        return selectedQuoteStyle() != state.quoteStyle ||
                ethEnabledCheckbox.isSelected != state.ethEnabled ||
                btcEnabledCheckbox.isSelected != state.btcEnabled ||
                solEnabledCheckbox.isSelected != state.solEnabled ||
                ethInclude0xCheckbox.isSelected != state.ethInclude0x
    }

    override fun apply() {
        val state = BlockchainToolsSettings.getInstance().state
        state.quoteStyle = selectedQuoteStyle()
        state.ethEnabled = ethEnabledCheckbox.isSelected
        state.btcEnabled = btcEnabledCheckbox.isSelected
        state.solEnabled = solEnabledCheckbox.isSelected
        state.ethInclude0x = ethInclude0xCheckbox.isSelected
    }

    override fun reset() {
        val state = BlockchainToolsSettings.getInstance().state
        when (state.quoteStyle) {
            BlockchainToolsSettings.QuoteStyle.NONE -> noneRadio.isSelected = true
            BlockchainToolsSettings.QuoteStyle.SINGLE -> singleRadio.isSelected = true
            BlockchainToolsSettings.QuoteStyle.DOUBLE -> doubleRadio.isSelected = true
        }
        ethEnabledCheckbox.isSelected = state.ethEnabled
        btcEnabledCheckbox.isSelected = state.btcEnabled
        solEnabledCheckbox.isSelected = state.solEnabled
        ethInclude0xCheckbox.isSelected = state.ethInclude0x
    }

    private fun selectedQuoteStyle() = when {
        singleRadio.isSelected -> BlockchainToolsSettings.QuoteStyle.SINGLE
        doubleRadio.isSelected -> BlockchainToolsSettings.QuoteStyle.DOUBLE
        else -> BlockchainToolsSettings.QuoteStyle.NONE
    }
}
