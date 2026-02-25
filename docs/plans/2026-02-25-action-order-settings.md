# Action Order Settings Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a Settings > Tools > Blockchain Tools page with a drag-and-drop list that persists the popup menu action order.

**Architecture:** A `BlockchainToolsSettings` app-service stores the action order as a list of IDs. `BlockchainActionsGroup` replaces the static `DefaultActionGroup`, reading from settings and assigning `_1.`/`_2.`/`_3.` mnemonics by position. `BlockchainToolsConfigurable` provides the settings UI.

**Tech Stack:** Kotlin, IntelliJ Platform SDK (`PersistentStateComponent`, `Configurable`, `JBList`, `TransferHandler`)

---

### Task 1: Create `BlockchainToolsSettings` service

**Files:**
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsSettings.kt`
- Test: `src/test/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsSettingsTest.kt`

**Step 1: Write the failing test**

```kotlin
// src/test/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsSettingsTest.kt
package com.github.ekinof.blockchaintools.settings

import junit.framework.TestCase

class BlockchainToolsSettingsTest : TestCase() {

    fun testDefaultOrderContainsAllThreeActions() {
        val settings = BlockchainToolsSettings()
        assertEquals(3, settings.state.actionOrder.size)
        assertTrue(settings.state.actionOrder.contains("com.github.ekinof.blockchaintools.GenerateAddressAction"))
        assertTrue(settings.state.actionOrder.contains("com.github.ekinof.blockchaintools.ChecksumAddressAction"))
        assertTrue(settings.state.actionOrder.contains("com.github.ekinof.blockchaintools.ToggleCaseAddressAction"))
    }

    fun testLoadStateRestoresCustomOrder() {
        val settings = BlockchainToolsSettings()
        val newOrder = mutableListOf(
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )
        settings.loadState(BlockchainToolsSettings.State(newOrder))
        assertEquals(newOrder, settings.state.actionOrder)
    }

    fun testGetStateRoundTrip() {
        val settings = BlockchainToolsSettings()
        val state = settings.state
        val settings2 = BlockchainToolsSettings()
        settings2.loadState(state)
        assertEquals(state.actionOrder, settings2.state.actionOrder)
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*.BlockchainToolsSettingsTest"`
Expected: FAIL — class not found

**Step 3: Write minimal implementation**

```kotlin
// src/main/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsSettings.kt
package com.github.ekinof.blockchaintools.settings

import com.intellij.openapi.components.*

@Service(Service.Level.APP)
@State(
    name = "BlockchainToolsSettings",
    storages = [Storage("blockchain-tools.xml")]
)
class BlockchainToolsSettings : PersistentStateComponent<BlockchainToolsSettings.State> {

    data class State(
        var actionOrder: MutableList<String> = DEFAULT_ORDER.toMutableList()
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        val DEFAULT_ORDER = listOf(
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )

        fun getInstance(): BlockchainToolsSettings = service()
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*.BlockchainToolsSettingsTest"`
Expected: PASS (3 tests)

**Step 5: Register the service in `plugin.xml`**

In `src/main/resources/META-INF/plugin.xml`, add inside `<extensions defaultExtensionNs="com.intellij">`:

```xml
<applicationService
    serviceImplementation="com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings"/>
```

**Step 6: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsSettings.kt \
        src/test/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsSettingsTest.kt \
        src/main/resources/META-INF/plugin.xml
git commit -m "feat: add BlockchainToolsSettings persistent state service"
```

---

### Task 2: Replace static group with `BlockchainActionsGroup`

**Files:**
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroup.kt`
- Modify: `src/main/resources/META-INF/plugin.xml`
- Test: `src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroupTest.kt`

**Step 1: Write the failing test**

```kotlin
// src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroupTest.kt
package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class BlockchainActionsGroupTest : BasePlatformTestCase() {

    fun testDefaultOrderProducesThreeChildren() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertEquals(3, children.size)
    }

    fun testChildrenHavePositionBasedMnemonics() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[0].templatePresentation.text.startsWith("_1."))
        assertTrue(children[1].templatePresentation.text.startsWith("_2."))
        assertTrue(children[2].templatePresentation.text.startsWith("_3."))
    }

    fun testCustomOrderChangesChildSequence() {
        val settings = BlockchainToolsSettings()
        settings.loadState(BlockchainToolsSettings.State(mutableListOf(
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )))

        val group = BlockchainActionsGroup(settings)
        val children = group.getChildren(null)
        assertTrue(children[0].templatePresentation.text.contains("Checksum"))
        assertTrue(children[1].templatePresentation.text.contains("Generate"))
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*.BlockchainActionsGroupTest"`
Expected: FAIL — class not found

**Step 3: Write minimal implementation**

```kotlin
// src/main/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroup.kt
package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.intellij.openapi.actionSystem.*

class BlockchainActionsGroup(
    private val settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : ActionGroup() {

    private val actionNames = mapOf(
        "com.github.ekinof.blockchaintools.GenerateAddressAction" to "Generate ERC20 Address",
        "com.github.ekinof.blockchaintools.ChecksumAddressAction" to "Checksum Address",
        "com.github.ekinof.blockchaintools.ToggleCaseAddressAction" to "Toggle Address Case"
    )

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val manager = ActionManager.getInstance()
        return settings.state.actionOrder.mapIndexed { index, id ->
            val action = manager.getAction(id) ?: return@mapIndexed null
            val text = "_${index + 1}. ${actionNames[id] ?: id}"
            object : AnAction(text) {
                override fun actionPerformed(e: AnActionEvent) = action.actionPerformed(e)
                override fun update(e: AnActionEvent) {
                    action.update(e)
                    e.presentation.text = text
                }
            }
        }.filterNotNull().toTypedArray()
    }
}
```

**Step 4: Update `plugin.xml`**

a) Change the group class and remove number prefixes from action text:

```xml
<group id="com.github.ekinof.blockchaintools.BlockchainActionsGroup"
       class="com.github.ekinof.blockchaintools.actions.BlockchainActionsGroup"
       popup="true"
       text="Blockchain Tools">
    <action id="com.github.ekinof.blockchaintools.GenerateAddressAction"
            class="com.github.ekinof.blockchaintools.actions.GenerateAddressAction"
            text="Generate ERC20 Address"
            description="Insert a random EIP-55 checksummed Ethereum address at the caret">
        <keyboard-shortcut keymap="$default" first-keystroke="1"/>
    </action>
    <action id="com.github.ekinof.blockchaintools.ChecksumAddressAction"
            class="com.github.ekinof.blockchaintools.actions.ChecksumAddressAction"
            text="Checksum Address"
            description="Verify the EIP-55 checksum of the selected address">
        <keyboard-shortcut keymap="$default" first-keystroke="2"/>
    </action>
    <action id="com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
            class="com.github.ekinof.blockchaintools.actions.ToggleCaseAddressAction"
            text="Toggle Address Case"
            description="Toggle the selected address between lowercase and EIP-55 checksummed form">
        <keyboard-shortcut keymap="$default" first-keystroke="3"/>
    </action>
</group>
```

**Step 5: Run test to verify it passes**

Run: `./gradlew test --tests "*.BlockchainActionsGroupTest"`
Expected: PASS (3 tests)

**Step 6: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroup.kt \
        src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroupTest.kt \
        src/main/resources/META-INF/plugin.xml
git commit -m "feat: add BlockchainActionsGroup with dynamic ordering from settings"
```

---

### Task 3: Create `BlockchainToolsConfigurable` settings page

**Files:**
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsConfigurable.kt`
- Modify: `src/main/resources/META-INF/plugin.xml`

> Note: Settings UI components are not unit-testable without a full IDE instance. Manual testing via `./gradlew runIde` is sufficient here.

**Step 1: Write the implementation**

```kotlin
// src/main/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsConfigurable.kt
package com.github.ekinof.blockchaintools.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import javax.swing.*

class BlockchainToolsConfigurable : Configurable {

    private val actionNames = mapOf(
        "com.github.ekinof.blockchaintools.GenerateAddressAction" to "Generate ERC20 Address",
        "com.github.ekinof.blockchaintools.ChecksumAddressAction" to "Checksum Address",
        "com.github.ekinof.blockchaintools.ToggleCaseAddressAction" to "Toggle Address Case"
    )

    private val model = DefaultListModel<String>()
    private val list = JBList(model)

    override fun getDisplayName() = "Blockchain Tools"

    override fun createComponent(): JComponent {
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        list.dragEnabled = true
        list.dropMode = DropMode.INSERT
        list.transferHandler = DragDropTransferHandler()
        list.cellRenderer = ListCellRenderer { _, value, index, isSelected, hasFocus ->
            DefaultListCellRenderer().getListCellRendererComponent(
                list,
                "${index + 1}. ${actionNames[value] ?: value}",
                index,
                isSelected,
                hasFocus
            )
        }
        reset()

        val panel = JPanel(BorderLayout(0, 8))
        panel.add(JBLabel("Drag to reorder actions in the popup menu:"), BorderLayout.NORTH)
        panel.add(JBScrollPane(list), BorderLayout.CENTER)
        return panel
    }

    override fun isModified(): Boolean {
        val current = (0 until model.size).map { model.getElementAt(it) }
        return current != BlockchainToolsSettings.getInstance().state.actionOrder
    }

    override fun apply() {
        BlockchainToolsSettings.getInstance().state.actionOrder =
            (0 until model.size).map { model.getElementAt(it) }.toMutableList()
    }

    override fun reset() {
        model.clear()
        BlockchainToolsSettings.getInstance().state.actionOrder.forEach { model.addElement(it) }
    }

    private inner class DragDropTransferHandler : TransferHandler() {
        private val flavor = DataFlavor(Int::class.java, "actionIndex")

        override fun getSourceActions(c: JComponent) = MOVE

        override fun createTransferable(c: JComponent): Transferable {
            val index = list.selectedIndex
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
```

**Step 2: Register the configurable in `plugin.xml`**

Add inside `<extensions defaultExtensionNs="com.intellij">`:

```xml
<applicationConfigurable
    parentId="tools"
    id="com.github.ekinof.blockchaintools.settings.BlockchainToolsConfigurable"
    instance="com.github.ekinof.blockchaintools.settings.BlockchainToolsConfigurable"
    displayName="Blockchain Tools"/>
```

**Step 3: Run the IDE and verify manually**

Run: `./gradlew runIde`

Checks:
- Open *File > Settings > Tools > Blockchain Tools* — list shows 3 actions
- Drag an item to a new position — order changes
- Click *Apply* — popup menu (`Ctrl+Alt+Shift+B`) reflects new order with correct `_1.`/`_2.`/`_3.` mnemonics
- Restart sandbox IDE — order is persisted

**Step 4: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsConfigurable.kt \
        src/main/resources/META-INF/plugin.xml
git commit -m "feat: add settings page to reorder blockchain actions via drag-and-drop"
```
