package george.tui.wizard

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal

class WizardScreen {
    private final WindowBasedTextGUI gui
    private final Window window
    private final QuestionZone questionZone
    private final Closure onNextCallback  // Callback function (String key -> void)

    WizardScreen(Screen screen, String title, DescriptionZone description, QuestionZone question, WizardButtonsZone buttons, Closure onNextCallback = { key -> }) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)
        this.questionZone = question
        this.onNextCallback = onNextCallback

        window.setHints([Window.Hint.EXPANDED] as Set)

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        panel.setFillColorOverride(TextColor.ANSI.BLUE)

        Component descriptionComponent = description.build()
        descriptionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        panel.addComponent(descriptionComponent)

        Component questionComponent = question.build()
        questionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        panel.addComponent(questionComponent)

        Component buttonsComponent = buttons.build(gui, this)
        buttonsComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        panel.addComponent(buttonsComponent)

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