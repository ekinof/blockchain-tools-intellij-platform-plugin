# Blockchain Address Tool Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a popup menu (default shortcut `Ctrl+Alt+B`) with three blockchain address utilities — Generate, Checksum, ToggleCase — to the IntelliJ plugin.

**Architecture:** Three `AnAction` classes registered under a `popup="true"` `DefaultActionGroup` in `plugin.xml`. A pure-Kotlin `EthAddressUtil` object handles all EIP-55 logic using Bouncy Castle for keccak256. Checksum result is shown as a balloon notification registered in `plugin.xml`.

**Tech Stack:** Kotlin, IntelliJ Platform SDK (`AnAction`, `WriteCommandAction`, `NotificationGroupManager`), Bouncy Castle (`bcprov-jdk15on`) for keccak256.

---

### Task 1: Add Bouncy Castle dependency

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts`

**Step 1: Add version entry to the version catalog**

In `gradle/libs.versions.toml`, add inside `[versions]`:

```toml
bouncycastle = "1.79"
```

And inside `[libraries]`:

```toml
bouncycastle = { group = "org.bouncycastle", name = "bcprov-jdk15on", version.ref = "bouncycastle" }
```

**Step 2: Add the dependency to build.gradle.kts**

Inside the `dependencies { }` block, after the existing `testImplementation` lines:

```kotlin
implementation(libs.bouncycastle)
```

**Step 3: Verify the dependency resolves**

```bash
./gradlew dependencies --configuration compileClasspath | grep bouncycastle
```

Expected output: a line containing `org.bouncycastle:bcprov-jdk15on:1.79`

**Step 4: Commit**

```bash
git add gradle/libs.versions.toml build.gradle.kts
git commit -m "build: add Bouncy Castle for keccak256 (EIP-55 checksum)"
```

---

### Task 2: Create EthAddressUtil — pure EIP-55 logic (TDD)

**Files:**
- Create: `src/test/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtilTest.kt`
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtil.kt`

**Step 1: Write the failing tests**

Create `src/test/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtilTest.kt`:

```kotlin
package com.github.ekinof.blockchaintools.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EthAddressUtilTest {

    // EIP-55 test vectors from the specification
    private val vectors = listOf(
        "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed",
        "0xfB6916095ca1df60bB79Ce92cE3Ea74c37c5d359",
        "0xdbF03B407c01E7cD3CBea99509d93f8DDDC8C6FB",
        "0xD1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb"
    )

    @Test
    fun `toChecksumAddress produces correct EIP-55 output for all spec vectors`() {
        for (v in vectors) {
            assertEquals(v, EthAddressUtil.toChecksumAddress(v.lowercase()))
            assertEquals(v, EthAddressUtil.toChecksumAddress(v.uppercase()))
            assertEquals(v, EthAddressUtil.toChecksumAddress(v))
        }
    }

    @Test
    fun `isValidChecksum returns true for correctly checksummed address`() {
        for (v in vectors) {
            assertTrue(EthAddressUtil.isValidChecksum(v))
        }
    }

    @Test
    fun `isValidChecksum returns false for lowercase address`() {
        assertFalse(EthAddressUtil.isValidChecksum(vectors[0].lowercase()))
    }

    @Test
    fun `isValidAddress accepts valid hex addresses regardless of case`() {
        assertTrue(EthAddressUtil.isValidAddress("0x5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"))
        assertTrue(EthAddressUtil.isValidAddress(vectors[0]))
    }

    @Test
    fun `isValidAddress rejects malformed addresses`() {
        assertFalse(EthAddressUtil.isValidAddress("5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"))  // no 0x
        assertFalse(EthAddressUtil.isValidAddress("0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAe"))   // too short
        assertFalse(EthAddressUtil.isValidAddress("0xGGGG"))                                       // invalid chars
        assertFalse(EthAddressUtil.isValidAddress(""))
    }

    @Test
    fun `toggleCase converts checksummed to lowercase`() {
        val checksummed = vectors[0]
        val lower = "0x" + checksummed.removePrefix("0x").lowercase()
        assertEquals(lower, EthAddressUtil.toggleCase(checksummed))
    }

    @Test
    fun `toggleCase converts lowercase to checksummed`() {
        val checksummed = vectors[0]
        val lower = "0x" + checksummed.removePrefix("0x").lowercase()
        assertEquals(checksummed, EthAddressUtil.toggleCase(lower))
    }

    @Test
    fun `generateAddress returns a valid checksummed Ethereum address`() {
        repeat(10) {
            val addr = EthAddressUtil.generateAddress()
            assertTrue("Expected valid address but got: $addr", EthAddressUtil.isValidAddress(addr))
            assertTrue("Expected checksummed address but got: $addr", EthAddressUtil.isValidChecksum(addr))
        }
    }
}
```

**Step 2: Run tests to confirm they fail**

```bash
./gradlew test --tests "com.github.ekinof.blockchaintools.util.EthAddressUtilTest" 2>&1 | tail -20
```

Expected: `FAILED` — `EthAddressUtil` does not exist yet.

**Step 3: Implement EthAddressUtil**

Create `src/main/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtil.kt`:

```kotlin
package com.github.ekinof.blockchaintools.util

import org.bouncycastle.crypto.digests.KeccakDigest
import java.security.SecureRandom

object EthAddressUtil {

    private val ADDRESS_REGEX = Regex("^0x[0-9a-fA-F]{40}$")

    fun isValidAddress(address: String): Boolean = ADDRESS_REGEX.matches(address)

    fun toChecksumAddress(address: String): String {
        val stripped = address.removePrefix("0x").lowercase()
        val hash = keccak256(stripped.toByteArray(Charsets.US_ASCII))
        val sb = StringBuilder("0x")
        for (i in stripped.indices) {
            val c = stripped[i]
            if (c in 'a'..'f') {
                val nibble = (hash[i / 2].toInt() ushr (if (i % 2 == 0) 4 else 0)) and 0xF
                sb.append(if (nibble >= 8) c.uppercaseChar() else c)
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun isValidChecksum(address: String): Boolean = address == toChecksumAddress(address)

    fun toggleCase(address: String): String {
        val checksummed = toChecksumAddress(address)
        return if (address == checksummed) {
            "0x" + address.removePrefix("0x").lowercase()
        } else {
            checksummed
        }
    }

    fun generateAddress(): String {
        val bytes = ByteArray(20)
        SecureRandom().nextBytes(bytes)
        val hex = bytes.joinToString("") { "%02x".format(it) }
        return toChecksumAddress("0x$hex")
    }

    private fun keccak256(input: ByteArray): ByteArray {
        val digest = KeccakDigest(256)
        digest.update(input, 0, input.size)
        val out = ByteArray(32)
        digest.doFinal(out, 0)
        return out
    }
}
```

**Step 4: Run tests to confirm they pass**

```bash
./gradlew test --tests "com.github.ekinof.blockchaintools.util.EthAddressUtilTest" 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL` — all 7 tests pass.

**Step 5: Commit**

```bash
git add src/test/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtilTest.kt \
        src/main/kotlin/com/github/ekinof/blockchaintools/util/EthAddressUtil.kt
git commit -m "feat: add EthAddressUtil with EIP-55 checksum, toggle, and address generation"
```

---

### Task 3: Register notification group and action group in plugin.xml

**Files:**
- Modify: `src/main/resources/META-INF/plugin.xml`

**Step 1: Add the notification group and action registration**

Replace the entire contents of `plugin.xml` with:

```xml
<!-- Plugin Configuration File. Read more: https://plugins.jetbrains.com/docs/intellij/plugin-configuration-file.html -->
<idea-plugin>
    <id>com.github.ekinof.blockchaintools</id>
    <name>blockchain-tools</name>
    <vendor>ekinof</vendor>

    <depends>com.intellij.modules.platform</depends>

    <resource-bundle>messages.MyBundle</resource-bundle>

    <extensions defaultExtensionNs="com.intellij">
        <toolWindow factoryClass="com.github.ekinof.blockchaintools.toolWindow.MyToolWindowFactory" id="MyToolWindow"/>
        <postStartupActivity implementation="com.github.ekinof.blockchaintools.startup.MyProjectActivity" />
        <notificationGroup id="BlockchainTools" displayType="BALLOON"/>
    </extensions>

    <actions>
        <group id="com.github.ekinof.blockchaintools.BlockchainActionsGroup"
               class="com.intellij.openapi.actionSystem.DefaultActionGroup"
               popup="true"
               text="Blockchain Tools">
            <action id="com.github.ekinof.blockchaintools.GenerateAddressAction"
                    class="com.github.ekinof.blockchaintools.actions.GenerateAddressAction"
                    text="Generate ERC20 Address"
                    description="Insert a random EIP-55 checksummed Ethereum address at the caret"/>
            <action id="com.github.ekinof.blockchaintools.ChecksumAddressAction"
                    class="com.github.ekinof.blockchaintools.actions.ChecksumAddressAction"
                    text="Checksum Address"
                    description="Verify the EIP-55 checksum of the selected address"/>
            <action id="com.github.ekinof.blockchaintools.ToggleCaseAddressAction"
                    class="com.github.ekinof.blockchaintools.actions.ToggleCaseAddressAction"
                    text="Toggle Address Case"
                    description="Toggle the selected address between lowercase and EIP-55 checksummed form"/>
            <keyboard-shortcut keymap="$default" first-keystroke="ctrl alt B"/>
        </group>
    </actions>
</idea-plugin>
```

**Step 2: Commit**

```bash
git add src/main/resources/META-INF/plugin.xml
git commit -m "feat: register blockchain action group with Ctrl+Alt+B shortcut"
```

---

### Task 4: Implement GenerateAddressAction (TDD)

**Files:**
- Create: `src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt`
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/GenerateAddressAction.kt`

**Step 1: Write the failing test**

Create `src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt`:

```kotlin
package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.testFramework.MapDataContext
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class BlockchainActionsTest : BasePlatformTestCase() {

    private fun makeEvent(action: com.intellij.openapi.actionSystem.AnAction): AnActionEvent {
        val context = MapDataContext()
        context.put(CommonDataKeys.PROJECT, project)
        context.put(CommonDataKeys.EDITOR, myFixture.editor)
        return AnActionEvent.createFromDataContext("test", action.templatePresentation, context)
    }

    fun testGenerateAddressInsertsValidChecksummedAddress() {
        myFixture.configureByText(PlainTextFileType.INSTANCE, "")
        val action = GenerateAddressAction()
        action.actionPerformed(makeEvent(action))
        val text = myFixture.editor.document.text
        assertTrue("Expected valid address, got: $text", EthAddressUtil.isValidAddress(text))
        assertTrue("Expected checksummed address, got: $text", EthAddressUtil.isValidChecksum(text))
    }
}
```

**Step 2: Run to confirm it fails**

```bash
./gradlew test --tests "com.github.ekinof.blockchaintools.actions.BlockchainActionsTest.testGenerateAddressInsertsValidChecksummedAddress" 2>&1 | tail -20
```

Expected: `FAILED` — `GenerateAddressAction` does not exist yet.

**Step 3: Implement GenerateAddressAction**

Create `src/main/kotlin/com/github/ekinof/blockchaintools/actions/GenerateAddressAction.kt`:

```kotlin
package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class GenerateAddressAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val address = EthAddressUtil.generateAddress()
        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.insertString(editor.caretModel.offset, address)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
```

**Step 4: Run test to confirm it passes**

```bash
./gradlew test --tests "com.github.ekinof.blockchaintools.actions.BlockchainActionsTest.testGenerateAddressInsertsValidChecksummedAddress" 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL`.

**Step 5: Commit**

```bash
git add src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt \
        src/main/kotlin/com/github/ekinof/blockchaintools/actions/GenerateAddressAction.kt
git commit -m "feat: implement GenerateAddressAction — inserts random EIP-55 address at caret"
```

---

### Task 5: Implement ChecksumAddressAction (TDD)

**Files:**
- Modify: `src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt`
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/ChecksumAddressAction.kt`

**Context:** ChecksumAddressAction only shows a balloon notification — it never modifies the document. Tests verify the document is unchanged after invocation (the actual notification content is validated indirectly via the `EthAddressUtilTest` that already covers `isValidChecksum`).

**Step 1: Add tests for ChecksumAddressAction**

Add these two test methods inside `BlockchainActionsTest`:

```kotlin
fun testChecksumActionDoesNotModifyDocumentForValidAddress() {
    val validChecksummed = "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed"
    myFixture.configureByText(PlainTextFileType.INSTANCE, validChecksummed)
    myFixture.editor.selectionModel.setSelection(0, validChecksummed.length)
    val action = ChecksumAddressAction()
    action.actionPerformed(makeEvent(action))
    assertEquals(validChecksummed, myFixture.editor.document.text)
}

fun testChecksumActionDoesNotModifyDocumentForInvalidAddress() {
    val lowercase = "0x5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"
    myFixture.configureByText(PlainTextFileType.INSTANCE, lowercase)
    myFixture.editor.selectionModel.setSelection(0, lowercase.length)
    val action = ChecksumAddressAction()
    action.actionPerformed(makeEvent(action))
    assertEquals(lowercase, myFixture.editor.document.text)
}
```

**Step 2: Run to confirm they fail**

```bash
./gradlew test --tests "com.github.ekinof.blockchaintools.actions.BlockchainActionsTest" 2>&1 | tail -20
```

Expected: `FAILED` — `ChecksumAddressAction` does not exist.

**Step 3: Implement ChecksumAddressAction**

Create `src/main/kotlin/com/github/ekinof/blockchaintools/actions/ChecksumAddressAction.kt`:

```kotlin
package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project

class ChecksumAddressAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText
        if (selectedText == null) {
            notify(e.project, "Select an Ethereum address first", NotificationType.WARNING)
            return
        }
        if (!EthAddressUtil.isValidAddress(selectedText)) {
            notify(e.project, "Not a valid Ethereum address", NotificationType.ERROR)
            return
        }
        if (EthAddressUtil.isValidChecksum(selectedText)) {
            notify(e.project, "✓ Valid EIP-55 checksum", NotificationType.INFORMATION)
        } else {
            notify(e.project, "✗ Invalid checksum", NotificationType.WARNING)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }

    private fun notify(project: Project?, message: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("BlockchainTools")
            .createNotification(message, type)
            .notify(project)
    }
}
```

**Step 4: Run all action tests to confirm they pass**

```bash
./gradlew test --tests "com.github.ekinof.blockchaintools.actions.BlockchainActionsTest" 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL`.

**Step 5: Commit**

```bash
git add src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt \
        src/main/kotlin/com/github/ekinof/blockchaintools/actions/ChecksumAddressAction.kt
git commit -m "feat: implement ChecksumAddressAction — validates EIP-55 checksum with balloon notification"
```

---

### Task 6: Implement ToggleCaseAddressAction (TDD)

**Files:**
- Modify: `src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt`
- Create: `src/main/kotlin/com/github/ekinof/blockchaintools/actions/ToggleCaseAddressAction.kt`

**Step 1: Add tests for ToggleCaseAddressAction**

Add these test methods inside `BlockchainActionsTest`:

```kotlin
fun testToggleCaseConvertsChecksummedToLowercase() {
    val checksummed = "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed"
    val expected = "0x5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"
    myFixture.configureByText(PlainTextFileType.INSTANCE, checksummed)
    myFixture.editor.selectionModel.setSelection(0, checksummed.length)
    val action = ToggleCaseAddressAction()
    action.actionPerformed(makeEvent(action))
    assertEquals(expected, myFixture.editor.document.text)
}

fun testToggleCaseConvertsLowercaseToChecksummed() {
    val lowercase = "0x5aaeb6053f3e94c9b9a09f33669435e7ef1beaed"
    val expected = "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed"
    myFixture.configureByText(PlainTextFileType.INSTANCE, lowercase)
    myFixture.editor.selectionModel.setSelection(0, lowercase.length)
    val action = ToggleCaseAddressAction()
    action.actionPerformed(makeEvent(action))
    assertEquals(expected, myFixture.editor.document.text)
}

fun testToggleCaseDoesNothingWithNoSelection() {
    val text = "no address here"
    myFixture.configureByText(PlainTextFileType.INSTANCE, text)
    val action = ToggleCaseAddressAction()
    action.actionPerformed(makeEvent(action))
    assertEquals(text, myFixture.editor.document.text)
}
```

**Step 2: Run to confirm they fail**

```bash
./gradlew test --tests "com.github.ekinof.blockchaintools.actions.BlockchainActionsTest" 2>&1 | tail -20
```

Expected: `FAILED` — `ToggleCaseAddressAction` does not exist.

**Step 3: Implement ToggleCaseAddressAction**

Create `src/main/kotlin/com/github/ekinof/blockchaintools/actions/ToggleCaseAddressAction.kt`:

```kotlin
package com.github.ekinof.blockchaintools.actions

import com.github.ekinof.blockchaintools.util.EthAddressUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project

class ToggleCaseAddressAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText
        if (selectedText == null) {
            notify(e.project, "Select an Ethereum address first", NotificationType.WARNING)
            return
        }
        if (!EthAddressUtil.isValidAddress(selectedText)) {
            notify(e.project, "Not a valid Ethereum address", NotificationType.ERROR)
            return
        }
        val toggled = EthAddressUtil.toggleCase(selectedText)
        WriteCommandAction.runWriteCommandAction(e.project) {
            editor.document.replaceString(
                selectionModel.selectionStart,
                selectionModel.selectionEnd,
                toggled
            )
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }

    private fun notify(project: Project?, message: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("BlockchainTools")
            .createNotification(message, type)
            .notify(project)
    }
}
```

**Step 4: Run all tests to confirm they pass**

```bash
./gradlew test --tests "com.github.ekinof.blockchaintools.actions.BlockchainActionsTest" 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL` — all toggle tests pass.

**Step 5: Run the full test suite**

```bash
./gradlew test 2>&1 | tail -30
```

Expected: `BUILD SUCCESSFUL` — all tests including the pre-existing `MyPluginTest` pass.

**Step 6: Commit**

```bash
git add src/test/kotlin/com/github/ekinof/blockchaintools/actions/BlockchainActionsTest.kt \
        src/main/kotlin/com/github/ekinof/blockchaintools/actions/ToggleCaseAddressAction.kt
git commit -m "feat: implement ToggleCaseAddressAction — toggles address between lowercase and EIP-55"
```

---

### Task 7: Final verification

**Step 1: Run full test suite one more time**

```bash
./gradlew test 2>&1 | tail -30
```

Expected: `BUILD SUCCESSFUL`.

**Step 2: Build the plugin**

```bash
./gradlew buildPlugin 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL` — a `.zip` is created under `build/distributions/`.

**Step 3: Commit (if any fixes were needed)**

If everything passed without changes, no commit needed. Otherwise:

```bash
git add -p
git commit -m "fix: address issues found during final verification"
```
