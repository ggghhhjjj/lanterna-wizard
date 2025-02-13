package george

import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import george.tui.wizard.DescriptionZone
import george.tui.wizard.QuestionZone
import george.tui.wizard.WizardScreen
import george.tui.wizard.Repository

class Main {
    static void main(String[] args) {
        Screen screen = new DefaultTerminalFactory().createScreen()
        screen.startScreen()

        // Set an initial variable in the Repository
        Repository.set("username", "Guest")

        WizardScreen welcomeScreen = new WizardScreen("Welcome", screen)
                .setDescription('Welcome, ${username}!\nPress Next to continue.')

        WizardScreen firstScreen = new WizardScreen("Step 1", screen)
                .setDescription('Hello ${username}, this is step 1. Your name is ${name}')
                .setQuestionZone(new QuestionZone("name", "What is your name?", "John Doe"))

        WizardScreen secondScreen = new WizardScreen("Step 2", screen)
                .setDescription('${name}, this is step 2.')
                .setQuestionZone(new QuestionZone("color", "What is your favorite color?", "Blue"))

        welcomeScreen.setNavigation(WizardScreen.NO_NAVIGATION, { firstScreen.show() })
        firstScreen.setNavigation({ welcomeScreen.show() }, { secondScreen.show() })
        secondScreen.setNavigation({ firstScreen.show() }) // No Next → Shows Finish button

        welcomeScreen.show()
        screen.stopScreen()
    }
}