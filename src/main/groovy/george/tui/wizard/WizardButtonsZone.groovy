package george.tui.wizard

import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen

class WizardButtonsZone {
    private final boolean hasNext
    private final boolean hasPrev
    private final Closure onNext
    private final Closure onBack

    WizardButtonsZone(boolean hasNext, boolean hasPrev, Closure onNext, Closure onBack) {
        this.hasNext = hasNext
        this.hasPrev = hasPrev
        this.onNext = onNext
        this.onBack = onBack
    }

    Component build(WindowBasedTextGUI gui, WizardScreen currentScreen) {
        Panel panel = new Panel(new GridLayout(3))

        GridLayout gridLayout = (GridLayout) panel.getLayoutManager()
        gridLayout.setTopMarginSize(1)

        // Exit Button
        Button exitButton = new Button("Exit", {
            Screen screen = gui.getScreen() // Get the screen from GUI
            screen.clear()  // Clear the screen
            screen.refresh() // Apply changes
            System.exit(0)
        })
        panel.addComponent(exitButton)

        // Back Button (if applicable)
        if (hasPrev) {
            Button backButton = new Button("Back", {
                currentScreen.saveData()
                onBack.call()
            })
            panel.addComponent(backButton)
        } else {
            panel.addComponent(new EmptySpace())
        }

        // Next or Finish Button
        Button nextFinishButton
        if (hasNext) {
            nextFinishButton = new Button("Next", {
                currentScreen.saveData()
                onNext.call()
            })
        } else {
            nextFinishButton = new Button("Finish", {
                currentScreen.saveData()
                gui.getActiveWindow().close()  // Close the active window
                gui.getWindows().forEach { it.close() } // Close all windows
                System.exit(0)
            })
        }
        panel.addComponent(nextFinishButton)

        return panel
    }
}