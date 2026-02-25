package com.github.ekinof.blockchaintools.settings

import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.GraphicsEnvironment
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import javax.swing.*

class BlockchainToolsConfigurable : Configurable {

    private val model = DefaultListModel<String>()
    private val list = JBList(model)
    private val cellRenderer = DefaultListCellRenderer()

    override fun getDisplayName() = "Blockchain Tools"

    override fun createComponent(): JComponent {
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        if (!GraphicsEnvironment.isHeadless()) {
            list.dragEnabled = true
            list.dropMode = DropMode.INSERT
            list.transferHandler = DragDropTransferHandler(model, list)
        }
        list.cellRenderer = ListCellRenderer { _, value, index, isSelected, hasFocus ->
            val label = ActionManager.getInstance().getAction(value)?.templatePresentation?.text ?: value
            cellRenderer.getListCellRendererComponent(list, "${index + 1}. $label", index, isSelected, hasFocus)
        }
        reset()

        val panel = JPanel(BorderLayout(0, 8))
        panel.add(JBLabel(BlockchainToolsBundle.message("settings.reorder_label")), BorderLayout.NORTH)
        panel.add(JBScrollPane(list), BorderLayout.CENTER)
        return panel
    }

    override fun isModified(): Boolean {
        val current = (0 until model.size).map { model.getElementAt(it) }
        val saved = BlockchainToolsSettings.getInstance().state.actionOrder.toList()
        return current != saved
    }

    override fun apply() {
        BlockchainToolsSettings.getInstance().state.actionOrder =
            (0 until model.size).map { model.getElementAt(it) }.toMutableList()
    }

    override fun reset() {
        model.clear()
        BlockchainToolsSettings.getInstance().state.actionOrder.forEach { model.addElement(it) }
    }

    private class DragDropTransferHandler(
        private val model: DefaultListModel<String>,
        private val list: JBList<String>
    ) : TransferHandler() {
        private val flavor = DataFlavor(Int::class.java, "actionIndex")

        override fun getSourceActions(c: JComponent) = MOVE

        override fun createTransferable(c: JComponent): Transferable? {
            val index = list.selectedIndex
            if (index == -1) return null   // abort drag if nothing selected
            return object : Transferable {
                override fun getTransferDataFlavors() = arrayOf(flavor)
                override fun isDataFlavorSupported(f: DataFlavor) = f == flavor
                override fun getTransferData(f: DataFlavor): Any = index
            }
        }

        override fun canImport(support: TransferSupport) =
            support.isDrop && support.isDataFlavorSupported(flavor)

        override fun importData(support: TransferSupport): Boolean {
            if (!canImport(support)) return false
            val dl = support.dropLocation as? JList.DropLocation ?: return false
            val from = support.transferable.getTransferData(flavor) as Int
            var to = dl.index
            if (from == to || to == from + 1) return false
            val item = model.remove(from)
            if (to > from) to--
            model.add(to, item)
            list.selectedIndex = to
            return true
        }
    }
}
