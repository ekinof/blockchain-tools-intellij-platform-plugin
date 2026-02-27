package com.github.ekinof.blockchaintools.settings

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class BlockchainToolsConfigurable : Configurable {

    private val noneRadio = JRadioButton(BlockchainToolsBundle.message("settings.quote.none"))
    private val singleRadio = JRadioButton(BlockchainToolsBundle.message("settings.quote.single"))
    private val doubleRadio = JRadioButton(BlockchainToolsBundle.message("settings.quote.double"))
    private val quoteGroup = ButtonGroup()
    private val include0xCheckbox = JCheckBox(BlockchainToolsBundle.message("settings.include_0x"))

    override fun getDisplayName() = "Blockchain Tools"

    override fun createComponent(): JComponent {
        quoteGroup.add(noneRadio)
        quoteGroup.add(singleRadio)
        quoteGroup.add(doubleRadio)
        reset()

        val quotePanel = JPanel()
        quotePanel.layout = BoxLayout(quotePanel, BoxLayout.X_AXIS)
        quotePanel.add(JBLabel(BlockchainToolsBundle.message("settings.quote_label")))
        quotePanel.add(Box.createHorizontalStrut(8))
        quotePanel.add(noneRadio)
        quotePanel.add(singleRadio)
        quotePanel.add(doubleRadio)

        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            anchor = GridBagConstraints.WEST
            insets = Insets(4, 0, 4, 0)
            gridx = 0
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }
        gbc.gridy = 0
        panel.add(quotePanel, gbc)
        gbc.gridy = 1
        panel.add(include0xCheckbox, gbc)
        gbc.gridy = 2
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.BOTH
        panel.add(JPanel(), gbc)

        return panel
    }

    override fun isModified(): Boolean {
        val state = BlockchainToolsSettings.getInstance().state
        return selectedQuoteStyle() != state.generateAddressQuoteStyle ||
                include0xCheckbox.isSelected != state.generateAddressInclude0x
    }

    override fun apply() {
        val state = BlockchainToolsSettings.getInstance().state
        state.generateAddressQuoteStyle = selectedQuoteStyle()
        state.generateAddressInclude0x = include0xCheckbox.isSelected
    }

    override fun reset() {
        val state = BlockchainToolsSettings.getInstance().state
        when (state.generateAddressQuoteStyle) {
            BlockchainToolsSettings.QuoteStyle.NONE -> noneRadio.isSelected = true
            BlockchainToolsSettings.QuoteStyle.SINGLE -> singleRadio.isSelected = true
            BlockchainToolsSettings.QuoteStyle.DOUBLE -> doubleRadio.isSelected = true
        }
        include0xCheckbox.isSelected = state.generateAddressInclude0x
    }

    private fun selectedQuoteStyle() = when {
        singleRadio.isSelected -> BlockchainToolsSettings.QuoteStyle.SINGLE
        doubleRadio.isSelected -> BlockchainToolsSettings.QuoteStyle.DOUBLE
        else -> BlockchainToolsSettings.QuoteStyle.NONE
    }
}
