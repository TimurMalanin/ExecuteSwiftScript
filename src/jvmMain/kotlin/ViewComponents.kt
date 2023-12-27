import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

private val buttonSize = Modifier.size(200.dp, 40.dp)
private val commonPadding = 8.dp

@Composable
@Preview
fun App() {
    val codeText = remember { mutableStateOf("") }
    val outputFlow = remember { MutableStateFlow("") }
    val exitCode = remember { mutableStateOf<Int?>(null) }
    val resetExitCode: () -> Unit = {
        exitCode.value = null
    }

    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        CodeEditorAndOutputColumn(codeText, outputFlow, Modifier.weight(3f))

        ActionButtonsColumn(
            codeText,
            Modifier.weight(1f),
            outputFlow,
            exitCode,
            resetExitCode
        )
    }
}

@Composable
fun ActionButtonsColumn(
    codeText: MutableState<String>,
    modifier: Modifier = Modifier,
    outputFlow: MutableStateFlow<String>,
    exitCode: MutableState<Int?>,
    resetExitCode: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End, modifier = modifier.padding(8.dp)
    ) {
        val isScriptRunning = remember { mutableStateOf(false) }

        ActionButton("Choose file") {
            if (chooseSwiftFile(codeText)) {
                outputFlow.value = ""
                resetExitCode()
            }
        }
        ActionButton("Execute code") {
            CoroutineScope(Dispatchers.Default).launch {
                isScriptRunning.value = true
                outputFlow.value = ""
                try {
                    executeSwiftScript(codeText.value, outputFlow, exitCode)
                } finally {
                    isScriptRunning.value = false
                }
            }
        }
        ActionButton("Reset") {
            codeText.value = ""
            outputFlow.value = ""
            isScriptRunning.value = false
            exitCode.value = null
        }

        ScriptStatusIndicator(isScriptRunning, exitCode)
    }
}

@Composable
fun ScriptStatusIndicator(
    isScriptRunning: MutableState<Boolean>,
    exitCode: MutableState<Int?>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(commonPadding)
    ) {
        AnimatedVisibility(visible = isScriptRunning.value) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Text("Running...", modifier = Modifier.padding(start = commonPadding))
            }
        }
        AnimatedVisibility(visible = !isScriptRunning.value) {
            Text(text = getExitCodeMessage(exitCode), modifier = Modifier.padding(top = commonPadding))
        }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = buttonSize.padding(bottom = commonPadding)) {
        Text(text)
    }
}

@Composable
fun CodeEditorAndOutputColumn(
    codeText: MutableState<String>, outputFlow: MutableStateFlow<String>, modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.End, modifier = modifier.padding(8.dp)) {
        val modifierBox = Modifier.fillMaxWidth().padding(commonPadding).weight(1f).border(2.dp, Color.DarkGray)

        CodeEditorBox(codeText, modifierBox)
        OutputBox(outputFlow, modifierBox)
    }
}

@Composable
fun CodeEditorBox(
    codeText: MutableState<String>,
    modifierBox: Modifier
) {
    val focusRequester = remember { FocusRequester() }

    val verticalState = rememberScrollState()
    val horizontalState = rememberScrollState()
    val modifier = Modifier.padding(commonPadding).verticalScroll(verticalState).horizontalScroll(horizontalState)

    Box(modifierBox) {
        Text(
            text = if (codeText.value.isEmpty()) AnnotatedString("Type your code here or choose a file...")
            else highlightKeywords(codeText.value),
            color = if (codeText.value.isEmpty()) Color.Gray else Color.Unspecified,
            modifier = modifier.matchParentSize(),
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp
        )

        BasicTextField(
            value = codeText.value,
            onValueChange = { newText -> codeText.value = newText },
            textStyle = TextStyle(color = Color.Transparent, fontFamily = FontFamily.Monospace, fontSize = 16.sp),
            cursorBrush = SolidColor(Color.Black),
            modifier = modifier.focusRequester(focusRequester).testTag("codeEditor")
        )
    }
}

@Composable
fun OutputBox(
    outputFlow: MutableStateFlow<String>,
    modifier: Modifier
) {
    val verticalState = rememberScrollState()
    val horizontalState = rememberScrollState()
    val outputText = outputFlow.collectAsState()
    Box(modifier = modifier) {
        Text(
            text = outputText.value.ifEmpty { "Output will be shown here" },
            color = if (outputText.value.isEmpty()) Color.Gray else Color.Black,
            modifier = Modifier.padding(commonPadding).verticalScroll(verticalState).horizontalScroll(horizontalState)
        )
    }
}
