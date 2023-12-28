import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ViewTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ActionButton should update clicked state`() {
        var clicked = false
        composeTestRule.setContent {
            ActionButton("Test Button") { clicked = true }
        }

        composeTestRule.onNodeWithText("Test Button").performClick()

        assert(clicked)
    }

    @Test
    fun `type in CodeEditorBox should reflect text input`() {
        composeTestRule.setContent {
            CodeEditorBox(mutableStateOf(""), Modifier)
        }

        composeTestRule.onNodeWithTag("codeEditor").performClick()
        composeTestRule.onNodeWithTag("codeEditor").performTextInput("fun test() {}")
        composeTestRule.onNodeWithTag("codeEditor").assertTextEquals("fun test() {}")
    }

    @Test
    fun `update OutputBox should reflect new output`() {
        val outputFlow = MutableStateFlow("Test Output")

        composeTestRule.setContent {
            OutputBox(outputFlow, Modifier)
        }

        composeTestRule.onNodeWithText("Test Output").assertExists()

        outputFlow.value = "New Output"
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("New Output").assertExists()

        outputFlow.value = ""
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Output will be shown here").assertExists()
    }
}
