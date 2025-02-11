package george

import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory

class Main {
    static void main(String[] args) {
        Screen screen = new DefaultTerminalFactory().createScreen()
        screen.startScreen()

        // Create wizard screens. We declare variables first so that closures can reference them.
        WizardScreen firstScreen = null
        WizardScreen secondScreen = null

        firstScreen = new WizardScreen(
                screen,
                "Step 1",
                new DescriptionZone("Welcome to the wizard! This is step 1."),
                new QuestionZone("What is your name?", "John Doe"),
                new WizardButtonsZone(
                        true,   // hasNext: there is a next screen
                        false,  // hasPrev: no previous screen on step 1
                        { -> secondScreen.show() }, // onNext: show step 2
                        { } // onBack: not used in step 1
                )
        )

        secondScreen = new WizardScreen(
                screen,
                "Step 2",
                new DescriptionZone("This is step 2. Almost done!"),
                new QuestionZone("What is your favorite color?", "Blue"),
                new WizardButtonsZone(
                        false,  // hasNext: no further screen so will show Finish button
                        true,   // hasPrev: back button is available
                        { ->
                            println("Wizard completed!")
                            System.exit(0)
                        },
                        { -> firstScreen.show() } // onBack: go back to step 1
                )
        )

        firstScreen.show()
    }
}