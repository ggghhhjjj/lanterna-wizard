package george

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.TextColor

/**
 * Displays the wizard buttons in a horizontal layout.
 * Visually, the left button is intended to be the red Exit button,
 * the middle (if applicable) the yellow Back button,
 * and the right the green Next or Finish button.
 *
 * Note: Directly setting foreground colors and focusability on buttons is not supported
 * in Lanterna 3.1.1. To style buttons, customize the theme for your TextGUI.
 */
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

    Component build(WindowBasedTextGUI gui) {
        // Use a GridLayout with 3 columns to fix button positions.
        Panel panel = new Panel(new GridLayout(3))
        // Approximately 1/6 of the screen height (e.g., 3 rows):
        panel.setPreferredSize(new TerminalSize(40, 3))

        // Column 0: Exit button (visually left).
        Button exitButton = new Button("Exit", {
            System.exit(0)
        })
        // Removed: exitButton.setFocusable(false)
        panel.addComponent(exitButton)

        // Column 1: Back button if available, or an empty placeholder.
        if (hasPrev) {
            Button backButton = new Button("Back", {
                onBack.call()
            })
            panel.addComponent(backButton)
        } else {
            panel.addComponent(new EmptySpace())
        }

        // Column 2: Next or Finish button.
        Button nextFinishButton
        if (hasNext) {
            nextFinishButton = new Button("Next", {
                onNext.call()
            })
        } else {
            nextFinishButton = new Button("Finish", {
                onNext.call()
            })
        }
        panel.addComponent(nextFinishButton)

        return panel
    }
}