package george.tui.wizard

import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen

class WizardButtonsZone {
    private static final Closure EMPTY_CLOSURE = {}

    private final Closure onNext
    private final Closure onBack
    private final boolean hasNext
    private final boolean hasPrev

    WizardButtonsZone(Closure onNext = EMPTY_CLOSURE, Closure onBack = EMPTY_CLOSURE) {
        this.onNext = onNext ?: EMPTY_CLOSURE // Ensure it's not null
        this.onBack = onBack ?: EMPTY_CLOSURE  // Ensure it's not null
        this.hasNext = !isEmptyClosure(this.onNext)
        this.hasPrev = !isEmptyClosure(this.onBack)
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

    private static boolean isEmptyClosure(Closure closure) {
        return closure == null || closure.is(EMPTY_CLOSURE)  // Check if closure is empty
    }
}