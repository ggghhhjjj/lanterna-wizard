package george.tui.wizard

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*

/**
 * Displays a question with an input field.
 * The question text is shown above the input TextBox.*/
class Question {
    final String question
    final String key
    private final TextBox inputField

    /**
     * Constructor initializes the question and text field.
     * It retrieves the stored value from Repository or sets a default.*/
    Question(String key, String question, String defaultAnswer = "") {
        this.key = key
        this.question = question
        def initialValue = Repository.get(key, defaultAnswer)  // Fetch stored value
        this.inputField = new PasteableTextBox(initialValue)
    }

    /**
     * Builds the UI component.*/
    Component build() {
        def panel = new Panel(new GridLayout(1))
        def gridLayout = (GridLayout) panel.getLayoutManager()
        gridLayout.setTopMarginSize(1)

        def label = new Label(question)
        label.setForegroundColor(TextColor.ANSI.RED)
        panel.addComponent(label, GridLayout.createHorizontallyFilledLayoutData())

        panel.addComponent(new EmptySpace())

        panel.addComponent(inputField, GridLayout.createHorizontallyFilledLayoutData())

        panel.addComponent(new Label('(ctr+c to paste if supported)'))

        return panel
    }

    /**
     * Saves user input to the repository.*/
    void saveAnswer() {
        Repository.set(key, inputField.getText())
    }

    /**
     * Gets the current input value.*/
    String getAnswer() {
        return inputField.getText()
    }
}