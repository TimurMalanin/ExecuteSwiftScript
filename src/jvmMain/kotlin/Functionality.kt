import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.nio.file.Files

val keywords = setOf("fun", "for", "if", "else", "return", "while", "break", "continue", "class", "try", "catch")

fun highlightKeywords(code: String): AnnotatedString {
    val builder = AnnotatedString.Builder()

    val regex = """[\s,.;:(){}\[\]+\-*/=<>!&|^%?#]+""".toRegex()

    val words = code.split(regex).filter { it.isNotEmpty() }
    var cursorPosition = 0

    for (word in words) {
        val textBeforeWord = code.substring(cursorPosition, code.indexOf(word, cursorPosition))
        builder.append(textBeforeWord)
        cursorPosition += textBeforeWord.length

        val color = if (word in keywords) Color.Blue else Color.Black
        builder.withStyle(style = SpanStyle(color = color)) {
            append(word)
        }
        cursorPosition += word.length
    }

    if (cursorPosition < code.length) {
        builder.append(code.substring(cursorPosition))
    }

    return builder.toAnnotatedString()
}

fun executeSwiftScript(
    code: String,
    outputFlow: MutableStateFlow<String>,
    exitCodeState: MutableState<Int?>
) {
    val tempScript = Files.createTempFile(null, ".swift").toFile()
    tempScript.writeText(code)
    tempScript.deleteOnExit()

    val process = ProcessBuilder("/usr/bin/env", "swift", tempScript.absolutePath).start()

    val reader = process.inputStream.bufferedReader()
    val errorReader = process.errorStream.bufferedReader()

    reader.forEachLine { line ->
        outputFlow.value += "$line\n"
    }

    errorReader.forEachLine { line ->
        outputFlow.value += "$line\n"
    }

    val exitValue = process.waitFor()
    exitCodeState.value = exitValue
}

fun chooseSwiftFile(codeText: MutableState<String>): Boolean {
    val frame = Frame().apply { isVisible = false }
    var fileSelected: String? = null

    with(FileDialog(frame, "Choose a .swift file", FileDialog.LOAD)) {
        setFilenameFilter { _, name -> name.endsWith(".swift") }
        isVisible = true
        fileSelected = if (file != null) "$directory$file" else null
    }

    frame.dispose()

    fileSelected?.let {
        val content = File(it).readText()
        codeText.value = content
    }

    return !fileSelected.isNullOrEmpty()
}

fun getExitCodeMessage(exitCode: MutableState<Int?>): String {
    return when (val code = exitCode.value) {
        null -> "No runs yet"
        0 -> "Last run was successful"
        else -> "Last run failed with exit code $code"
    }
}