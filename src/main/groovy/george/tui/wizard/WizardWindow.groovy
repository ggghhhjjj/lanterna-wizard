package george.tui.wizard

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen

class WizardWindow {
    static final Closure NO_NAVIGATION = {}

    private final WindowBasedTextGUI gui
    private final Window window
    private Description descriptionZone
    private Question questionZone
    private final Navigation buttonsZone
    private final Panel mainPanel

    private static final int DESCRIPTION_INDEX = 0
    private static final int QUESTION_INDEX = 1

    WizardWindow(String title, Screen screen) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)
        this.descriptionZone = new Description("") // Default empty description
        this.questionZone = null // Default null
        this.buttonsZone = new Navigation(gui, this)

        window.setHints([Window.Hint.EXPANDED] as Set)

        // Initialize main panel
        this.mainPanel = new Panel(new LinearLayout(Direction.VERTICAL))
        this.mainPanel.setFillColorOverride(TextColor.ANSI.BLUE)

        // Add description zone
        Component descriptionComponent = descriptionZone.build()
        descriptionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        this.mainPanel.addComponent(descriptionComponent)

        // Add placeholder for QuestionZone
        Component questionComponent = new EmptySpace()
        questionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        this.mainPanel.addComponent(questionComponent)

        // Add buttons
        Component buttonsComponent = buttonsZone.build()
        buttonsComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        this.mainPanel.addComponent(buttonsComponent)

        window.setComponent(mainPanel)
    }

    /** Replaces the DescriptionZone and refreshes the UI */
    WizardWindow setDescription(String text) {
        def newDescription = new Description(text)
        mainPanel.removeComponent(mainPanel.children[DESCRIPTION_INDEX])

        this.descriptionZone = newDescription
        Component newComponent = newDescription.build()
        newComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        mainPanel.addComponent(DESCRIPTION_INDEX, newComponent)

        gui.updateScreen()
        return this
    }

    /** Replaces the QuestionZone and refreshes the UI */
    WizardWindow setQuestionZone(Question newQuestionZone) {
        mainPanel.removeComponent(mainPanel.children[QUESTION_INDEX])

        this.questionZone = newQuestionZone
        Component newComponent = (newQuestionZone != null) ? newQuestionZone.build() : new EmptySpace()
        newComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        mainPanel.addComponent(QUESTION_INDEX, newComponent)

        gui.updateScreen()
        return this
    }

    /** Updates the navigation buttons */
    WizardWindow setNavigation(Closure onBack, Closure onNext = NO_NAVIGATION) {
        buttonsZone.setNavigation(onBack, onNext)
        gui.updateScreen()
        return this
    }

    void show() {
        gui.addWindowAndWait(window)
    }
    void close() {
        window.close()
    }

    /**
     * Returns the answer for the current question from the Repository.
     * @throws IllegalStateException if no QuestionZone is present.
     */
    String getQuestionAnswer() {
        if (questionZone == null) {
            throw new IllegalStateException("No question is set for this WizardScreen.")
        }
        return Repository.get(questionZone.key, "")
    }

    private void saveData() {
        if (questionZone != null) questionZone.saveAnswer()
    }
}