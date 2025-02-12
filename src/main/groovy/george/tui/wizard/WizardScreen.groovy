package george.tui.wizard

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.TextColor

class WizardScreen {
    private final WindowBasedTextGUI gui
    private final Window window
    private final QuestionZone questionZone
    private final Closure onNextCallback  // Callback function (String key -> void)

    WizardScreen(Screen screen, String title, DescriptionZone description, QuestionZone question, WizardButtonsZone buttons, Closure onNextCallback = { key -> }) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)
        this.questionZone = question
        this.onNextCallback = onNextCallback  // Store the callback function

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        panel.setPreferredSize(new TerminalSize(40, 18))
        panel.setFillColorOverride(TextColor.ANSI.BLUE)

        panel.addComponent(description.build())
        panel.addComponent(question.build())
        panel.addComponent(buttons.build(gui, this))  // Pass WizardScreen instance

        window.setComponent(panel)
    }

    void show() {
        gui.addWindowAndWait(window)
    }

    void close() {
        window.close()
    }

    void saveData() {
        questionZone.saveAnswer()  // Save input to repository
        onNextCallback.call(questionZone.key)  // Execute the callback function
    }
}