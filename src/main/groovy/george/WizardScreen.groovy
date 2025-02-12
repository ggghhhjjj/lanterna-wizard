package george

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.TextColor

class WizardScreen {
    private final WindowBasedTextGUI gui
    private final Window window
    private final QuestionZone questionZone

    WizardScreen(Screen screen, String title, DescriptionZone description, QuestionZone question, WizardButtonsZone buttons) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)
        this.questionZone = question  // Store the question zone reference

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        panel.setPreferredSize(new TerminalSize(40, 18))
        panel.setFillColorOverride(TextColor.ANSI.BLUE)

        panel.addComponent(description.build())
        panel.addComponent(question.build())
        panel.addComponent(buttons.build(gui, this))  // Pass the screen instance

        window.setComponent(panel)
    }

    void show() {
        gui.addWindowAndWait(window)
    }

    void close() {
        window.close()
    }

    void saveData() {
        questionZone.saveAnswer()  // Save input to repository before switching screens
    }
}