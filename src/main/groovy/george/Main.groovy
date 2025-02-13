package george

import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import george.tui.wizard.Question
import george.tui.wizard.WizardWindow
import george.tui.wizard.Repository

class Main {
    static void main(String[] args) {
        Screen screen = new DefaultTerminalFactory().createScreen()
        screen.startScreen()

        // Set an initial variable in the Repository
        Repository.set("username", "Guest")

        WizardWindow welcomeScreen = new WizardWindow("Welcome", screen)
                .setDescription('Welcome, ${username}!\nPress Next to continue.')

        WizardWindow firstScreen = new WizardWindow("Step 1", screen)
                .setDescription('Hello ${username}, this is step 1. Your name is ${name}')
                .setQuestionZone(new Question("name", "What is your name?", "John Doe"))

        WizardWindow secondScreen = new WizardWindow("Step 2", screen)
                .setDescription('${name}, this is step 2.')
                .setQuestionZone(new Question("color", "What is your favorite color?", "Blue"))

        WizardWindow finalScreen = new WizardWindow("Summary", screen)
                .setDescription('Thank you, ${name}! You like ${color}.')


        WizardWindow processScreen = new WizardWindow("Process Output", screen)
                .setDescription("Click 'Next' to execute a command and view its output.")

        welcomeScreen.setNavigation(WizardWindow.NO_NAVIGATION, { firstScreen.show() })

        // Navigation with answer retrieval
        firstScreen.setNavigation({ welcomeScreen.show() }, {
            println("User entered name: " + firstScreen.getQuestionAnswer())
            secondScreen.show()
        })

        secondScreen.setNavigation({ firstScreen.show() }, {
            println("User entered color: " + secondScreen.getQuestionAnswer())
            processScreen.show()
        })

        processScreen.setNavigation({secondScreen.show()}, {
            String os = System.getProperty("os.name").toLowerCase()
            def command = os.contains("win") ? ["dir"] : ["ls", "-lah"]
            processScreen.showProcessOutputPopup(command, "Process Output")
            finalScreen.show()
        })

        finalScreen.setNavigation({ processScreen.show() })

        welcomeScreen.show()
        screen.stopScreen()
    }
}