package george.tui.wizard

import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.TextColor

class WizardScreen {
    private final WindowBasedTextGUI gui
    private final Window window
    private final QuestionZone questionZone

    /**
     * Constructor for WizardScreen.
     *
     * @param screen the Lanterna screen
     * @param title the window title
     * @param description the DescriptionZone component
     * @param questionZone the QuestionZone component (can be null if not needed)
     * @param buttons the WizardButtonsZone component
     * @param onNextCallback callback to invoke when Next is pressed
     */
    WizardScreen(Screen screen, String title, DescriptionZone description, QuestionZone question, WizardButtonsZone buttons) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)
        this.questionZone = question

        window.setHints([Window.Hint.EXPANDED] as Set)

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        panel.setFillColorOverride(TextColor.ANSI.BLUE)

        Component descriptionComponent = description.build()
        descriptionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        panel.addComponent(descriptionComponent)

        // Conditionally add QuestionZone if provided
        if (questionZone != null) {
            Component questionComponent = questionZone.build()
            questionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
            panel.addComponent(questionComponent)
        }

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
        if (questionZone != null) {
            questionZone.saveAnswer()  // Save input to repository
        }
    }
}