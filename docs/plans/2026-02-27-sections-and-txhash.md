# Sections and ETH TxHash Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Split the Blockchain Tools popup into "EIP-55 Address" and "ETH TxHash" sections with separators, and add a Generate TxHash action that reuses the existing quote/0x settings.

**Architecture:** Add `generateTxHash()` to `EthAddressUtil`, create `GenerateTxHashAction` mirroring `GenerateAddressAction`, update `BlockchainActionsGroup` to insert named `Separator` objects, and register the new action in `plugin.xml`.

**Tech Stack:** Kotlin, IntelliJ Platform SDK (`DefaultActionGroup`, `Separator`), JUnit4 / `BasePlatformTestCase`

---

### Task 1: Add `generateTxHash()` to `EthAddressUtil`

**Files:**
- Modify: `src/main/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtil.kt`
- Test: `src/test/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtilTest.kt`

**Step 1: Write the failing test**

Add this test to `EthAddressUtilTest`:

```kotlin
@Test
fun `generateTxHash returns a valid 0x-prefixed 64-char lowercase hex string`() {
    repeat(10) {
        val hash = EthAddressUtil.generateTxHash()
        assertTrue("Expected 0x prefix, got: $hash", hash.startsWith("0x"))
        assertEquals("Expected 66 chars total, got: $hash", 66, hash.length)
        val hex = hash.removePrefix("0x")
        assertTrue("Expected only hex chars, got: $hex", hex.all { it in '0'..'9' || it in 'a'..'f' })
    }
}
```

**Step 2: Run test to verify it fails**

```bash
./gradlew test --tests "*.EthAddressUtilTest.generateTxHash*"
```
Expected: FAIL — `Unresolved reference: generateTxHash`

**Step 3: Implement `generateTxHash`**

Add after `generateAddress()` in `EthAddressUtil.kt`:

```kotlin
fun generateTxHash(): String {
    val bytes = ByteArray(32)
    SecureRandom().nextBytes(bytes)
    return "0x" + bytes.joinToString("") { "%02x".format(it) }
}
```

**Step 4: Run test to verify it passes**

```bash
./gradlew test --tests "*.EthAddressUtilTest.generateTxHash*"
```
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtil.kt \
        src/test/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtilTest.kt
git commit -m "feat: add generateTxHash to EthAddressUtil"
```

---

### Task 2: Create `GenerateTxHashAction`

**Files:**
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/GenerateTxHashAction.kt`
- Test: `src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt`

**Step 1: Write the failing tests**

Add to `BlockchainActionsTest.kt` (reuse the existing `settingsWith` helper):

```kotlin
fun testGenerateTxHashInsertsValid0xHash() {
    myFixture.configureByText(PlainTextFileType.INSTANCE, "")
    val action = GenerateTxHashAction(settingsWith())
    action.actionPerformed(makeEvent(action))
    val text = myFixture.editor.document.text
    assertTrue("Expected 0x prefix", text.startsWith("0x"))
    assertEquals("Expected 66 chars", 66, text.length)
    assertTrue("Expected hex chars", text.removePrefix("0x").all { it in '0'..'9' || it in 'a'..'f' })
}

fun testGenerateTxHashWithout0x() {
    myFixture.configureByText(PlainTextFileType.INSTANCE, "")
    val action = GenerateTxHashAction(settingsWith(include0x = false))
    action.actionPerformed(makeEvent(action))
    val text = myFixture.editor.document.text
    assertFalse("Expected no 0x prefix", text.startsWith("0x"))
    assertEquals(64, text.length)
}

fun testGenerateTxHashWithDoubleQuotes() {
    myFixture.configureByText(PlainTextFileType.INSTANCE, "")
    val action = GenerateTxHashAction(settingsWith(quoteStyle = BlockchainToolsSettings.QuoteStyle.DOUBLE))
    action.actionPerformed(makeEvent(action))
    val text = myFixture.editor.document.text
    assertTrue(text.startsWith("\"") && text.endsWith("\""))
    val inner = text.removeSurrounding("\"")
    assertTrue(inner.startsWith("0x"))
    assertEquals(66, inner.length)
}
```

**Step 2: Run tests to verify they fail**

```bash
./gradlew test --tests "*.BlockchainActionsTest.testGenerateTxHash*"
```
Expected: FAIL — `Unresolved reference: GenerateTxHashAction`

**Step 3: Create `GenerateTxHashAction.kt`**

```kotlin
package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.settings.BlockchainToolsSettings
import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class GenerateTxHashAction(
    private val settings: BlockchainToolsSettings = BlockchainToolsSettings.getInstance()
) : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val state = settings.state
        var hash = EthAddressUtil.generateTxHash()
        if (!state.generateAddressInclude0x) {
            hash = hash.removePrefix("0x")
        }
        val output = when (state.generateAddressQuoteStyle) {
            BlockchainToolsSettings.QuoteStyle.SINGLE -> "'$hash'"
            BlockchainToolsSettings.QuoteStyle.DOUBLE -> "\"$hash\""
            BlockchainToolsSettings.QuoteStyle.NONE -> hash
        }
        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.insertString(editor.caretModel.offset, output)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
```

**Step 4: Run tests to verify they pass**

```bash
./gradlew test --tests "*.BlockchainActionsTest.testGenerateTxHash*"
```
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/actions/GenerateTxHashAction.kt \
        src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt
git commit -m "feat: add GenerateTxHashAction"
```

---

### Task 3: Register the new action in `plugin.xml`

**Files:**
- Modify: `src/main/resources/META-INF/plugin.xml`

**Step 1: Add action registration**

Inside the `<group>` block, after `ToggleCaseAddressAction`, add:

```xml
<action id="com.github.ekinof.blockchaintools.GenerateTxHashAction"
        class="com.github.ekinof.blockchaintools.actions.GenerateTxHashAction"
        text="Generate TxHash"
        description="Insert a random Ethereum transaction hash at the caret">
    <keyboard-shortcut keymap="$default" first-keystroke="4"/>
</action>
```

**Step 2: Verify the build compiles**

```bash
./gradlew compileKotlin
```
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add src/main/resources/META-INF/plugin.xml
git commit -m "feat: register GenerateTxHashAction in plugin.xml"
```

---

### Task 4: Update `BlockchainActionsGroup` with sections and separators

**Files:**
- Modify: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroup.kt`
- Test: `src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroupTest.kt`

**Step 1: Write the failing tests**

Replace `BlockchainActionsGroupTest.kt` entirely:

```kotlin
package com.github.ekinof.blockchaintools.actions

import com.intellij.openapi.actionSystem.Separator
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class BlockchainActionsGroupTest : BasePlatformTestCase() {

    fun testGroupHasTwoSeparatorsAndFourActions() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        // Separator + 3 address actions + Separator + 1 txhash action = 6 total
        assertEquals(6, children.size)
    }

    fun testFirstChildIsEip55Separator() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[0] is Separator)
        assertEquals("EIP-55 Address", (children[0] as Separator).text)
    }

    fun testAddressActionsAreNumbered1To3() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[1].templatePresentation.text.startsWith("1."))
        assertTrue(children[1].templatePresentation.text.contains("Generate"))
        assertTrue(children[2].templatePresentation.text.startsWith("2."))
        assertTrue(children[2].templatePresentation.text.contains("Checksum"))
        assertTrue(children[3].templatePresentation.text.startsWith("3."))
        assertTrue(children[3].templatePresentation.text.contains("Toggle"))
    }

    fun testFifthChildIsEthTxHashSeparator() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[4] is Separator)
        assertEquals("ETH TxHash", (children[4] as Separator).text)
    }

    fun testTxHashActionIsNumbered4() {
        val group = BlockchainActionsGroup()
        val children = group.getChildren(null)
        assertTrue(children[5].templatePresentation.text.startsWith("4."))
        assertTrue(children[5].templatePresentation.text.contains("TxHash"))
    }
}
```

**Step 2: Run tests to verify they fail**

```bash
./gradlew test --tests "*.BlockchainActionsGroupTest"
```
Expected: FAIL — wrong size or missing separators

**Step 3: Update `BlockchainActionsGroup.kt`**

```kotlin
package com.github.ekinof.blockchaintools.actions

import com.intellij.openapi.actionSystem.*

class BlockchainActionsGroup : DefaultActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val manager = ActionManager.getInstance()
        val result = mutableListOf<AnAction>()
        var counter = 1

        result.add(Separator("EIP-55 Address"))
        for (id in ADDRESS_ACTIONS) {
            val action = manager.getAction(id) ?: continue
            val text = "${counter++}. ${action.templatePresentation.text}"
            result.add(wrapWithText(action, text))
        }

        result.add(Separator("ETH TxHash"))
        for (id in TXHASH_ACTIONS) {
            val action = manager.getAction(id) ?: continue
            val text = "${counter++}. ${action.templatePresentation.text}"
            result.add(wrapWithText(action, text))
        }

        return result.toTypedArray()
    }

    private fun wrapWithText(action: AnAction, text: String): AnAction =
        object : AnAction() {
            init {
                templatePresentation.setText(text, false)
                copyShortcutFrom(action)
            }
            override fun actionPerformed(e: AnActionEvent) = action.actionPerformed(e)
            override fun update(e: AnActionEvent) {
                action.update(e)
                e.presentation.setText(text, false)
            }
        }

    companion object {
        val ADDRESS_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateAddressAction",
            "com.github.ekinof.blockchaintools.ChecksumAddressAction",
            "com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
        )
        val TXHASH_ACTIONS = listOf(
            "com.github.ekinof.blockchaintools.GenerateTxHashAction"
        )
    }
}
```

**Step 4: Run all tests**

```bash
./gradlew test
```
Expected: all tests PASS

**Step 5: Commit**

```bash
git add src/main/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroup.kt \
        src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsGroupTest.kt
git commit -m "feat: add EIP-55 Address and ETH TxHash sections to popup"
```
