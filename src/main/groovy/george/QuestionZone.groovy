package george

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*

/**
 * Displays a question with an input field.
 * The question text is shown above the input TextBox.
 */
class QuestionZone {
    private final String question
    private final String defaultAnswer
    private final TextBox inputField

    QuestionZone(String question, String defaultAnswer = "") {
        this.question = question
        this.defaultAnswer = defaultAnswer
        this.inputField = new TextBox(new TerminalSize(30, 1), defaultAnswer)
    }

    Component build() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        // Approximately 1/6 of the screen height (e.g., 3 rows):
        panel.setPreferredSize(new TerminalSize(40, 3))
        panel.addComponent(new Label(question))
        panel.addComponent(inputField)
        return panel
    }

    String getAnswer() {
        return inputField.getText()
    }
}