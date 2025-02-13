package george.tui.wizard

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen

class WizardScreen {
    static final Closure NO_NAVIGATION = {}

    private final WindowBasedTextGUI gui
    private final Window window
    private final QuestionZone questionZone
    private WizardButtonsZone buttonsZone

    WizardScreen(Screen screen, String title, DescriptionZone description, QuestionZone questionZone = null) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)
        this.questionZone = questionZone
        this.buttonsZone = new WizardButtonsZone(gui, this) // Default buttonsZone

        window.setHints([Window.Hint.EXPANDED] as Set)

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        panel.setFillColorOverride(TextColor.ANSI.BLUE)

        // Description Component
        Component descriptionComponent = description.build()
        descriptionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        panel.addComponent(descriptionComponent)

        // Conditionally add QuestionZone
        if (questionZone != null) {
            Component questionComponent = questionZone.build()
            questionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
            panel.addComponent(questionComponent)
        }

        // Add buttons
        Component buttonsComponent = buttonsZone.build()
        buttonsComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        panel.addComponent(buttonsComponent)

        window.setComponent(panel)
    }

    /**
     * Adds a Next button callback and updates the WizardButtonsZone.*/
    WizardScreen addNavigation(Closure onBack, Closure onNext = NO_NAVIGATION) {
        this.buttonsZone.addNavigation(onBack, onNext)
        return this
    }

    /**
     * Adds a Back button callback and updates the WizardButtonsZone.*/
    WizardScreen onBack(Closure callback) {
        this.buttonsZone.onBack = callback
        return this
    }

    void show() {
        gui.addWindowAndWait(window)
    }

    void close() {
        window.close()
    }

    void saveData() {
        if (questionZone != null) {
            questionZone.saveAnswer()
        }
    }

    /**
     * Private inner class for handling wizard buttons.*/
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

        void addNavigation(Closure onBack, Closure onNext = NO_NAVIGATION) {
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