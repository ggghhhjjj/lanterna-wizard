package george.tui.wizard

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*

/**
 * Displays a question with an input field.
 * The question text is shown above the input TextBox.
 */
class QuestionZone {
    private final String question
    private final String key
    private final TextBox inputField

    /**
     * Constructor initializes the question and text field.
     * It retrieves the stored value from Repository or sets a default.
     */
    QuestionZone(String key, String question, String defaultAnswer = "") {
        this.key = key
        this.question = question
        String initialValue = Repository.get(key, defaultAnswer)  // Fetch stored value
        this.inputField = new TextBox(new TerminalSize(30, 1), initialValue)
    }

    /**
     * Builds the UI component.
     */
    Component build() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        panel.setPreferredSize(new TerminalSize(40, 3))
        panel.addComponent(new Label(question))
        panel.addComponent(inputField)
        return panel
    }

    /**
     * Saves user input to the repository.
     */
    void saveAnswer() {
        Repository.set(key, inputField.getText())
    }

    /**
     * Gets the current input value.
     */
    String getAnswer() {
        return inputField.getText()
    }
}