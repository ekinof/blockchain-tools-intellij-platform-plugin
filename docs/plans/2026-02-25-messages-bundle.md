# Messages Bundle Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Move all hardcoded user-visible strings from Kotlin source into an IntelliJ `DynamicBundle` messages bundle.

**Architecture:** Create a `BlockchainToolsBundle.properties` file under `src/main/resources/messages/` and a companion `BlockchainToolsBundle` Kotlin object wrapping `DynamicBundle`. Replace all hardcoded strings in action and settings files with `BlockchainToolsBundle.message("key")`. Remove the redundant `actionNames` map from `BlockchainToolsConfigurable` — names are already available via `ActionManager` at runtime.

**Tech Stack:** IntelliJ Platform `DynamicBundle`, Kotlin object, Java `.properties` format

---

### Task 1: Create the properties file and bundle object

**Files:**
- Create: `src/main/resources/messages/BlockchainToolsBundle.properties`
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/BlockchainToolsBundle.kt`

**Step 1: Create the properties file**

```properties
# src/main/resources/messages/BlockchainToolsBundle.properties

# ChecksumAddressAction / ToggleCaseAddressAction
action.error.no_selection=Select an Ethereum address first
action.error.invalid_address=Not a valid Ethereum address

# ChecksumAddressAction
action.checksum.valid=\u2713 Valid EIP-55 checksum
action.checksum.invalid=\u2717 Invalid checksum

# ShowBlockchainActionsAction popup title
popup.title=Blockchain Tools

# BlockchainToolsConfigurable
settings.reorder_label=Drag to reorder actions in the popup menu:
```

**Step 2: Create the bundle object**

```kotlin
// src/main/kotlin/com/github/ekinof/blockchaintools/BlockchainToolsBundle.kt
package com.github.ekinof.blockchaintools

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "messages.BlockchainToolsBundle"

object BlockchainToolsBundle : DynamicBundle(BUNDLE) {
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String =
        getMessage(key, *params)
}
```

**Step 3: Run the build to verify compilation**

```bash
./gradlew compileKotlin
```

Expected: BUILD SUCCESSFUL — both new files compile cleanly.

**Step 4: Commit**

```bash
git add src/main/resources/messages/BlockchainToolsBundle.properties \
        src/main/kotlin/com/github/ekinof/blockchaintools/BlockchainToolsBundle.kt
git commit -m "feat: add DynamicBundle messages bundle"
```

---

### Task 2: Update ChecksumAddressAction

**Files:**
- Modify: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/ChecksumAddressAction.kt`
- Test: `src/test/kotlin/com/github/ekinof/blockchaintools/actions/ChecksumAddressActionTest.kt` (if exists)

**Step 1: Replace hardcoded strings**

Change the four string literals to bundle lookups:

```kotlin
import com.github.ekinof.blockchaintools.BlockchainToolsBundle

// inside actionPerformed:
notify(e.project, BlockchainToolsBundle.message("action.error.no_selection"), NotificationType.WARNING)
notify(e.project, BlockchainToolsBundle.message("action.error.invalid_address"), NotificationType.ERROR)
notify(e.project, BlockchainToolsBundle.message("action.checksum.valid"), NotificationType.INFORMATION)
notify(e.project, BlockchainToolsBundle.message("action.checksum.invalid"), NotificationType.WARNING)
```

**Step 2: Build and verify no regressions**

```bash
./gradlew build -x buildSearchableOptions
```

Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/actions/ChecksumAddressAction.kt
git commit -m "refactor: use messages bundle in ChecksumAddressAction"
```

---

### Task 3: Update ToggleCaseAddressAction

**Files:**
- Modify: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/ToggleCaseAddressAction.kt`

**Step 1: Replace hardcoded strings**

```kotlin
import com.github.ekinof.blockchaintools.BlockchainToolsBundle

// inside actionPerformed:
notify(e.project, BlockchainToolsBundle.message("action.error.no_selection"), NotificationType.WARNING)
notify(e.project, BlockchainToolsBundle.message("action.error.invalid_address"), NotificationType.ERROR)
```

**Step 2: Build and verify**

```bash
./gradlew build -x buildSearchableOptions
```

Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/actions/ToggleCaseAddressAction.kt
git commit -m "refactor: use messages bundle in ToggleCaseAddressAction"
```

---

### Task 4: Update ShowBlockchainActionsAction

**Files:**
- Modify: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/ShowBlockchainActionsAction.kt`

**Step 1: Replace popup title string**

```kotlin
import com.github.ekinof.blockchaintools.BlockchainToolsBundle

// inside actionPerformed:
val popup = PopupFactoryImpl.getInstance()
    .createActionGroupPopup(BlockchainToolsBundle.message("popup.title"), group, e.dataContext, null, true)
```

**Step 2: Build and verify**

```bash
./gradlew build -x buildSearchableOptions
```

Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/actions/ShowBlockchainActionsAction.kt
git commit -m "refactor: use messages bundle in ShowBlockchainActionsAction"
```

---

### Task 5: Update BlockchainToolsConfigurable — label + remove actionNames map

**Files:**
- Modify: `src/main/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsConfigurable.kt`

**Step 1: Replace label string and remove `actionNames` map**

The `actionNames` map duplicates action text already defined in `plugin.xml`. Remove it and read names directly from `ActionManager` — which `BlockchainActionsGroup.getChildren()` already does.

Remove the entire `actionNames` field and update `createComponent` and the cell renderer:

```kotlin
import com.github.ekinof.blockchaintools.BlockchainToolsBundle
import com.intellij.openapi.actionSystem.ActionManager

// Remove the actionNames map entirely.

// In createComponent, update the cell renderer:
list.cellRenderer = ListCellRenderer { _, value, index, isSelected, hasFocus ->
    val label = ActionManager.getInstance().getAction(value)?.templatePresentation?.text ?: value
    cellRenderer.getListCellRendererComponent(list, "${index + 1}. $label", index, isSelected, hasFocus)
}

// Replace the label:
panel.add(JBLabel(BlockchainToolsBundle.message("settings.reorder_label")), BorderLayout.NORTH)
```

**Step 2: Build and verify**

```bash
./gradlew build -x buildSearchableOptions
```

Expected: BUILD SUCCESSFUL

**Step 3: Final full build (including searchable options)**

```bash
./gradlew build
```

Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/settings/BlockchainToolsConfigurable.kt
git commit -m "refactor: use messages bundle in BlockchainToolsConfigurable, read action names from ActionManager"
```