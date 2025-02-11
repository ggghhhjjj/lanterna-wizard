package george

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.TextColor

/**
 * The main wizard screen which assembles the three zones.
 * The overall background is set to blue.
 */
class WizardScreen {
    private final WindowBasedTextGUI gui
    private final Window window

    WizardScreen(Screen screen, String title, DescriptionZone description, QuestionZone question, WizardButtonsZone buttons) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)

        // Main panel with blue background.
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        // Overall size is 40 columns x 18 rows (for example):
        panel.setPreferredSize(new TerminalSize(40, 18))
        panel.setFillColorOverride(TextColor.ANSI.BLUE) // Use setFillColorOverride instead of setBackgroundColor

        // Add zones (order: description, question, wizard buttons)
        panel.addComponent(description.build())
        panel.addComponent(question.build())
        panel.addComponent(buttons.build(gui))

        window.setComponent(panel)
    }

    void show() {
        gui.addWindowAndWait(window)
    }

    void close() {
        window.close()
    }
}