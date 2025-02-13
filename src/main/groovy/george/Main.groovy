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

        WizardScreen welcomeScreen = new WizardScreen("Welcome", screen)
                .setDescription("Welcome to the Wizard!\nPress Next to continue.")

        WizardScreen firstScreen = new WizardScreen("Step 1", screen)
                .setDescription("This is step 1.")
                .setQuestionZone(new QuestionZone("name", "What is your name?", "John Doe"))

        WizardScreen secondScreen = new WizardScreen("Step 2", screen)
                .setDescription("This is step 2.")
                .setQuestionZone(new QuestionZone("color", "What is your favorite color?", "Blue"))

        welcomeScreen.setNavigation(WizardScreen.NO_NAVIGATION, { firstScreen.show() })
        firstScreen.setNavigation({ welcomeScreen.show() }, { secondScreen.show() })
        secondScreen.setNavigation({ firstScreen.show() }) // No Next → Shows Finish button

        welcomeScreen.show()
        screen.stopScreen()
    }
}