import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.jupiter.api.Assertions

class FunctionalityTests {

    @Test
    fun `highlightKeywords should highlight keywords correctly`() {
        val input = "fun if else"
        val result = highlightKeywords(input)

        val expected = AnnotatedString.Builder().apply {
            pushStyle(SpanStyle(color = Color.Blue))
            append("fun")
            pop()
            append(" ")
            pushStyle(SpanStyle(color = Color.Blue))
            append("if")
            pop()
            append(" ")
            pushStyle(SpanStyle(color = Color.Blue))
            append("else")
            pop()
        }.toAnnotatedString()

        assertEquals(expected, result)
    }

    @Test
    fun `highlightKeywords should handle non-keywords`() {
        val input = "println hello"
        val result = highlightKeywords(input)

        val expected = AnnotatedString("println hello")

        val nonKeywordParts = result.spanStyles.filter {
            it.item.color == Color.Blue
        }

        assertEquals(expected.text, result.text)
        assertTrue("Non-keywords should not have styles applied", nonKeywordParts.isEmpty())
    }


    @Test
    fun `highlightKeywords should handle empty input`() {
        val input = ""
        val result = highlightKeywords(input)

        val expected = AnnotatedString.Builder().apply {
            append("")
        }.toAnnotatedString()

        assertEquals(expected, result)
    }

    @Test
    fun `executeSwiftScript should update output and exit code for valid script`()   {
        val code = "print(\"Hello, Swift\")"
        val outputFlow = MutableStateFlow("")
        val exitCodeState = mutableStateOf<Int?>(null)

        executeSwiftScript(code, outputFlow, exitCodeState)

        Assertions.assertEquals("Hello, Swift\n", outputFlow.value)
        Assertions.assertEquals(0, exitCodeState.value)
    }

    @Test
    fun `executeSwiftScript should handle invalid script`() {
        val code = "invalid code"
        val outputFlow = MutableStateFlow("")
        val exitCodeState = mutableStateOf<Int?>(null)

        executeSwiftScript(code, outputFlow, exitCodeState)

        Assertions.assertNotEquals("Hello, Swift\n", outputFlow.value)
        Assertions.assertNotEquals(0, exitCodeState.value)
    }

    @Test
    fun `executeSwiftScript should handle empty script`() {
        val code = ""
        val outputFlow = MutableStateFlow("")
        val exitCodeState = mutableStateOf<Int?>(null)

        executeSwiftScript(code, outputFlow, exitCodeState)

        Assertions.assertEquals("", outputFlow.value)
        Assertions.assertEquals(0, exitCodeState.value)
    }
}
