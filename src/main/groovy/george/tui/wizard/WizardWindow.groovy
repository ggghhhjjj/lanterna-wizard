package george.tui.wizard

import com.googlecode.lanterna.TerminalSize
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

    void saveData() {
        if (questionZone != null) questionZone.saveAnswer()
    }

    /**
     * Displays a popup window showing real-time output from a process.
     * @param process The external process whose output should be displayed.
     * @param title The title of the popup window.
     */
    Process showProcessOutputPopup(List<String> command, String title) {
        Process process

        BasicWindow popup = new BasicWindow(title)
        popup.setHints([Window.Hint.EXPANDED] as Set)

        TextBox outputBox = new TextBox("", TextBox.Style.MULTI_LINE).with {
            setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
            setReadOnly(true)
        }

        // Scrollable panel containing the output TextBox
        Panel scrollablePanel = new Panel(new LinearLayout(Direction.VERTICAL)).with {
            setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
            addComponent(outputBox)
        }

        Button closeButton = new Button("Close", {
            if (!process.alive) {
                popup.close()
            } else {
                showTerminationConfirmation(popup, process)
            }
        })

        Panel popupPanel = new Panel(new LinearLayout(Direction.VERTICAL)).with {
            setFillColorOverride(TextColor.ANSI.BLACK)
            addComponent(scrollablePanel)
            addComponent(closeButton)
        }

        popup.setComponent(popupPanel)
        gui.addWindow(popup)
        gui.moveToTop(popup)

        process = command.execute()

        BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.inputStream, "UTF-8"))
        BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.errorStream, "UTF-8"))
        StringBuffer outputBuffer = new StringBuffer()

        Thread stdoutThread = startOutputThread(stdoutReader, outputBuffer, outputBox)
        Thread stderrThread = startOutputThread(stderrReader, outputBuffer, outputBox)

        stdoutThread.start()
        stderrThread.start()

        stdoutThread.join()
        stderrThread.join()

        gui.addWindowAndWait(popup)

        return process
    }

    private static Thread startOutputThread(stdoutReader, outputBuffer, outputBox, isErrorStream = false) {
        Thread stdoutThread = new Thread({
            String line
            while ((line = stdoutReader.readLine()) != null) {
                outputBuffer.append(isErrorStream ? "[ERROR] " : "").append(line).append("\n")
                outputBox.setText(outputBuffer.toString())
                outputBox.setCaretPosition(outputBox.getText().length()) // Auto-scroll
            }
            stdoutReader.close()
        })

        return stdoutThread
    }

    /**
     * Displays a confirmation dialog when the process is still running.
     * If the user chooses OK, the process is terminated and the popup closes.
     * If the user chooses Cancel, the alert closes but the process keeps running.
     */
    private void showTerminationConfirmation(Window popup, Process process) {
        BasicWindow alertWindow = new BasicWindow("Process Running")

        Panel alertPanel = new Panel(new LinearLayout(Direction.VERTICAL))
        alertPanel.addComponent(new Label("The process is still running. Do you want to terminate it?"))

        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL))

        Button okButton = new Button("OK", {
            process.destroy()
            popup.close()
            alertWindow.close()
        })

        Button cancelButton = new Button("Cancel", { alertWindow.close() })

        buttonPanel.addComponent(okButton)
        buttonPanel.addComponent(cancelButton)

        alertPanel.addComponent(buttonPanel)
        alertWindow.setComponent(alertPanel)
        alertWindow.setHints([Window.Hint.CENTERED] as Set)

        gui.addWindow(alertWindow)
    }
}