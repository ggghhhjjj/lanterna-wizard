package george.tui.wizard

import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class PasteableTextBox extends TextBox {
    PasteableTextBox(String initialText = "") {
        super(initialText)
    }

    @Override
    synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        // Detect Ctrl+V (Windows/Linux). Command+V (MacOS) is not supported
        if ((keyStroke.isCtrlDown() && keyStroke.getCharacter() == 'v')) {

            String clipboardText = getClipboardContents()
            if (clipboardText) {
                // Insert clipboard text at the current caret position
                int caretPos = getCaretPosition().getColumn()
                String text = getText()
                setText(text.substring(0, caretPos) + clipboardText + text.substring(caretPos))

                // Move caret forward after paste
                setCaretPosition(caretPos + clipboardText.length())
            }
            return Result.HANDLED // Prevent further processing
        }
        return super.handleKeyStroke(keyStroke)
    }

    private static String getClipboardContents() {
        try {
            def clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
            def transferable = clipboard.getContents(null)
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return transferable.getTransferData(DataFlavor.stringFlavor)
            }
        } catch (Exception ignore) {
            // nothing to do
        }
        return ""
    }
}