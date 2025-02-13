package george

import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import george.tui.wizard.DescriptionZone
import george.tui.wizard.QuestionZone
import george.tui.wizard.WizardScreen

class Main {
    static void main(String[] args) {
        Screen screen = new DefaultTerminalFactory().createScreen()
        screen.startScreen()

        WizardScreen welcomeScreen
        WizardScreen firstScreen
        WizardScreen secondScreen

        // Create the welcome WizardScreen without a QuestionZone.
        welcomeScreen = new WizardScreen(screen, "Welcome",
                new DescriptionZone("Welcome to the Wizard!\nPress Next to continue."),
                null)

        firstScreen = new WizardScreen(screen, "Step 1",
                new DescriptionZone("Welcome to the wizard! This is step 1."),
                new QuestionZone("name", "What is your name?", "John Doe"))

        secondScreen = new WizardScreen(screen, "Step 2",
                new DescriptionZone("This is step 2. Almost done!"),
                new QuestionZone("color", "What is your favorite color?", "Blue"))

        welcomeScreen.addNavigation(WizardScreen.NO_NAVIGATION, { firstScreen.show() })

        firstScreen.addNavigation({ welcomeScreen.show() }, { secondScreen.show() })

        secondScreen.addNavigation({ firstScreen.show() })

        welcomeScreen.show()
    }
}