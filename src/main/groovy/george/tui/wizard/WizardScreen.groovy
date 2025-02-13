package george.tui.wizard

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen

class WizardScreen {
    static final Closure NO_NAVIGATION = {}

    private final WindowBasedTextGUI gui
    private final Window window
    private DescriptionZone descriptionZone
    private QuestionZone questionZone
    private final WizardButtonsZone buttonsZone
    private final Panel mainPanel

    private static final int DESCRIPTION_INDEX = 0
    private static final int QUESTION_INDEX = 1
    private static final int BUTTONS_INDEX = 2

    WizardScreen(String title, Screen screen) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)
        this.descriptionZone = new DescriptionZone("") // Default empty description
        this.questionZone = null // Default null
        this.buttonsZone = new WizardButtonsZone(gui, this)

        window.setHints([Window.Hint.EXPANDED] as Set)

        // Initialize main panel
        this.mainPanel = new Panel(new LinearLayout(Direction.VERTICAL))
        this.mainPanel.setFillColorOverride(TextColor.ANSI.BLUE)

        // Add description zone
        Component descriptionComponent = descriptionZone.build()
        descriptionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        this.mainPanel.addComponent(descriptionComponent)

        // Add placeholder for QuestionZone
        Component questionComponent = new EmptySpace()
        questionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        this.mainPanel.addComponent(questionComponent)

        // Add buttons
        Component buttonsComponent = buttonsZone.build()
        buttonsComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        this.mainPanel.addComponent(buttonsComponent)

        window.setComponent(mainPanel)
    }

    /** Replaces the DescriptionZone and refreshes the UI */
    WizardScreen setDescription(String text) {
        def newDescription = new DescriptionZone(text)
        mainPanel.removeComponent(mainPanel.children[DESCRIPTION_INDEX])

        this.descriptionZone = newDescription
        Component newComponent = newDescription.build()
        newComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        mainPanel.addComponent(DESCRIPTION_INDEX, newComponent)

        gui.updateScreen()
        return this
    }

    /** Replaces the QuestionZone and refreshes the UI */
    WizardScreen setQuestionZone(QuestionZone newQuestionZone) {
        mainPanel.removeComponent(mainPanel.children[QUESTION_INDEX])

        this.questionZone = newQuestionZone
        Component newComponent = (newQuestionZone != null) ? newQuestionZone.build() : new EmptySpace()
        newComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        mainPanel.addComponent(QUESTION_INDEX, newComponent)

        gui.updateScreen()
        return this
    }

    /** Updates the navigation buttons */
    WizardScreen setNavigation(Closure onBack, Closure onNext = NO_NAVIGATION) {
        buttonsZone.setNavigation(onBack, onNext)
        gui.updateScreen()
        return this
    }

    void show() {
        gui.addWindowAndWait(window)
    }
    void close() {
        window.close()
    }
    void saveData() {
        if (questionZone != null) questionZone.saveAnswer()
    }

    /**
     * Private inner class for managing wizard navigation buttons.
     */
    private static class WizardButtonsZone {
        private static final int BACK_BUTTON_NDX = 1
        private static final int NEXT_BUTTON_NDX = 2
        private static final Closure EMPTY_CLOSURE = {}

        private final WindowBasedTextGUI gui
        private final WizardScreen currentScreen
        private Closure onNext = EMPTY_CLOSURE
        private Closure onBack = EMPTY_CLOSURE
        private Panel panel

        WizardButtonsZone(WindowBasedTextGUI gui, WizardScreen currentScreen) {
            this.gui = gui
            this.currentScreen = currentScreen
        }

        Component build() {
            panel = new Panel(new GridLayout(3))
                    .addComponent(new Button("Exit", { exitApplication() }))
                    .addComponent(new EmptySpace()) // Placeholder for Back button
                    .addComponent(new Button("Finish", { exitApplication() }))

            return panel
        }

        private void exitApplication() {
            gui.getScreen().clear()
            gui.getScreen().refresh()
            System.exit(0)
        }

        void setNavigation(Closure onBack, Closure onNext = NO_NAVIGATION) {
            this.onBack = onBack ?: NO_NAVIGATION
            this.onNext = onNext ?: NO_NAVIGATION

            if (onBack != NO_NAVIGATION) {
                updateButton(BACK_BUTTON_NDX, onBack, "Back")
            }

            if (onNext != NO_NAVIGATION) {
                updateButton(NEXT_BUTTON_NDX, onNext, "Next")
            }

            gui.updateScreen()
        }

        private void updateButton(int index, Closure action, String label) {
            panel.removeComponent(panel.children[index])
            panel.addComponent(index, new Button(label, {
                currentScreen.saveData()
                action.call()
            }))
        }
    }
}