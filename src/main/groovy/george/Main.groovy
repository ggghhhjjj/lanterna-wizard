package george


import george.tui.wizard.Question
import george.tui.wizard.Page
import george.tui.wizard.Repository
import george.tui.wizard.Wizard

class Main {
    static void main(String[] args) {
        def wizard = new Wizard()

        // Set an initial variable in the Repository
        Repository.set("username", "Guest")

        Page welcomeScreen = wizard.addPage("Welcome")
                .withDescription('Welcome, ${username}!\nPress Next to continue.')

        Page firstScreen = wizard.addPage("Step 1")
                .withDescription('Hello ${username}, this is step 1. Your name is ${name}')
                .withQuestion(new Question("name", "What is your name?", "John Doe"))

        Page secondScreen = wizard.addPage("Step 2")
                .withDescription('${name}, this is step 2.')
                .withQuestion(new Question("color", "What is your favorite color?", "Blue"))

        Page finalScreen = wizard.addPage("Summary")
                .withDescription('Thank you, ${name}! You like ${color}.')


        Page processScreen = wizard.addPage("Process Output")
                .withDescription("Click 'Next' to execute a command and view its output.")

        welcomeScreen.withNavigation(Page.NO_NAVIGATION, { firstScreen.show() })

        // Navigation with answer retrieval
        firstScreen.withNavigation({ welcomeScreen.show() }, {
            println("User entered name: " + firstScreen.getQuestionAnswer())
            secondScreen.show()
        })

        secondScreen.withNavigation({ firstScreen.show() }, {
            println("User entered color: " + secondScreen.getQuestionAnswer())
            processScreen.show()
        })

        processScreen.withNavigation({secondScreen.show()}, {
            String os = System.getProperty("os.name").toLowerCase()
            def command = os.contains("win") ? ["dir"] : ["ls", "-lah"]
            processScreen.showProcessOutputPopup(command, "Process Output")
            finalScreen.show()
        })

        finalScreen.withNavigation({ processScreen.show() })

        welcomeScreen.show()
        wizard.stop()
    }
}