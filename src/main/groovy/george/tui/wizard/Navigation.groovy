package george.tui.wizard

import com.googlecode.lanterna.gui2.Button
import com.googlecode.lanterna.gui2.Component
import com.googlecode.lanterna.gui2.EmptySpace
import com.googlecode.lanterna.gui2.GridLayout
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.WindowBasedTextGUI

/**
 * Private inner class for managing wizard navigation buttons.
 */
class Navigation {
    private static final int BACK_BUTTON_NDX = 1
    private static final int NEXT_BUTTON_NDX = 2
    private static final Closure EMPTY_CLOSURE = {}

    private final WindowBasedTextGUI gui
    private final WizardWindow currentScreen
    private Closure onNext = EMPTY_CLOSURE
    private Closure onBack = EMPTY_CLOSURE
    private Panel panel

    Navigation(WindowBasedTextGUI gui, WizardWindow currentScreen) {
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

    void setNavigation(Closure onBack, Closure onNext = WizardWindow.NO_NAVIGATION) {
        this.onBack = onBack ?: WizardWindow.NO_NAVIGATION
        this.onNext = onNext ?: WizardWindow.NO_NAVIGATION

        if (onBack != WizardWindow.NO_NAVIGATION) {
            updateButton(BACK_BUTTON_NDX, onBack, "Back")
        }

        if (onNext != WizardWindow.NO_NAVIGATION) {
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
