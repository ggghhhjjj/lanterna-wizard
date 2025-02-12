package george

import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import george.tui.wizard.DescriptionZone
import george.tui.wizard.QuestionZone
import george.tui.wizard.WizardButtonsZone
import george.tui.wizard.WizardScreen

class Main {
    static void main(String[] args) {
        Screen screen = new DefaultTerminalFactory().createScreen()
        screen.startScreen()

        WizardScreen firstScreen
        WizardScreen secondScreen

        firstScreen = new WizardScreen(
                screen, "Step 1",
                new DescriptionZone("Welcome to the wizard! This is step 1."),
                new QuestionZone("name", "What is your name?", "John Doe"),  // Unique key
                new WizardButtonsZone(
                true, false,
                { -> secondScreen.show() }, {
                }
                ), { key ->
                    println("Next pressed, Question Key: $key")
                }  // Custom callback
                )

        secondScreen = new WizardScreen(
                screen, "Step 2",
                new DescriptionZone("This is step 2. Almost done!"),
                new QuestionZone("color", "What is your favorite color?", "Blue"),  // Unique key
                new WizardButtonsZone(
                false, true,
                { -> println("Wizard completed!"); System.exit(0) }, {
                    -> firstScreen.show()
                }
                ), { key ->
                    println("Finish pressed, Question Key: $key")
                }  // Custom callback
                )

        firstScreen.show()
    }
}