package george.tui.wizard

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*

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
        panel.setPreferredSize(new TerminalSize(40, 3))

        // Exit Button
        Button exitButton = new Button("Exit", {
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
                System.exit(0)
            })
        }
        panel.addComponent(nextFinishButton)

        return panel
    }
}