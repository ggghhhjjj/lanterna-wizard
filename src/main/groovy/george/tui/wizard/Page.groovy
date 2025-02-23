package george.tui.wizard

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen

import java.nio.charset.StandardCharsets

/**
 * Represents a page in the wizard, consisting of a title, description, question area,
 * and navigation buttons. Each page is managed within a Lanterna-based Text User Interface (TUI).*/
class Page {
    /**
     * Default closure for when no navigation action is provided.*/
    static final Closure NO_NAVIGATION = {}

    private final WindowBasedTextGUI gui
    private final Window window
    private Description description
    private Question question
    private final Navigation buttons
    private final Panel mainPanel

    private static final int DESCRIPTION_INDEX = 0
    private static final int QUESTION_INDEX = 1

    /**
     * Constructs a new wizard page with a title and a screen.
     *
     * @param title The title of the page.
     * @param screen The screen on which the page will be displayed.
     */
    Page(String title, Screen screen) {
        this.gui = new MultiWindowTextGUI(screen)
        this.window = new BasicWindow(title)
        this.description = new Description("") // Default empty description
        this.question = null // Default null
        this.buttons = new Navigation(gui, this)

        window.setHints([Window.Hint.EXPANDED] as Set)

        // Initialize main panel
        this.mainPanel = new Panel(new LinearLayout(Direction.VERTICAL))
        this.mainPanel.setFillColorOverride(TextColor.ANSI.BLUE)

        // Add description zone
        Component descriptionComponent = description.build()
        descriptionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        this.mainPanel.addComponent(descriptionComponent)

        // Add placeholder for QuestionZone
        Component questionComponent = new EmptySpace()
        questionComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        this.mainPanel.addComponent(questionComponent)

        // Add buttons
        Component buttonsComponent = buttons.build()
        buttonsComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        this.mainPanel.addComponent(buttonsComponent)

        window.setComponent(mainPanel)
    }

    /**
     * Updates the description section of the page with a provided text.
     *
     * @param text The new description text.
     * @return The updated {@link Page} instance.
     */
    Page withDescription(String text) {
        updateDescription(new Description(text))
        return this
    }

    /**
     * Updates the description section of the page with text loaded from a file in the classpath.
     *
     * @param filePath The path to the text file in the classpath.
     * @return The updated {@link Page} instance.
     * @throws IllegalArgumentException If the file cannot be found or read.
     */
    Page withDescriptionFromFile(String filePath) {
        String fileContent = loadTextFromClasspath(filePath)
        return withDescription(fileContent)
    }

    /**
     * Sets a new question on the page.
     *
     * @param newQuestionZone The new question component.
     * @return The updated {@link Page} instance.
     */
    Page withQuestion(Question newQuestionZone) {
        mainPanel.removeComponent(mainPanel.children[QUESTION_INDEX])

        this.question = newQuestionZone
        Component newComponent = (newQuestionZone != null) ? newQuestionZone.build() : new EmptySpace()
        newComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.None))
        mainPanel.addComponent(QUESTION_INDEX, newComponent)

        gui.updateScreen()
        return this
    }

    /**
     * Configures the navigation buttons for the page.
     *
     * @param onBack The action to execute when navigating back.
     * @param onNext The action to execute when navigating forward (default: NO_NAVIGATION).
     * @return The updated {@link Page} instance.
     */
    Page withNavigation(Closure onBack, Closure onNext = NO_NAVIGATION) {
        buttons.setNavigation(onBack, onNext)
        gui.updateScreen()
        return this
    }

    /**
     * Displays the page on the screen.*/
    void show() {
        gui.addWindowAndWait(window)
    }

    /**
     * Closes the current page.*/
    void close() {
        window.close()
    }

    /**
     * Retrieves the answer to the current question from the repository.
     *
     * @return The stored answer for the current question.
     * @throws IllegalStateException If no question is set for this page.
     */
    String getQuestionAnswer() {
        if (question == null) {
            throw new IllegalStateException("No question is set for this WizardScreen.")
        }
        return Repository.get(question.key, "")
    }

    /**
     * Saves the current question's answer to the repository.*/
    void saveData() {
        if (question != null) question.saveAnswer()
    }

    /**
     * Displays a popup window that shows the real-time output of an external process.
     *
     * @param command The command to execute.
     * @param title The title of the popup window.
     * @return The process instance.
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

    /**
     * Creates a thread to capture the output from a process stream.
     *
     * @param stdoutReader The reader for the process output.
     * @param outputBuffer A buffer to store the output text.
     * @param outputBox The UI component displaying the output.
     * @param isErrorStream Whether the stream is an error stream.
     * @return The created thread.
     */
    private static Thread startOutputThread(stdoutReader, outputBuffer, outputBox, isErrorStream = false) {
        return new Thread({
            String line
            while ((line = stdoutReader.readLine()) != null) {
                outputBuffer.append(isErrorStream ? "[ERROR] " : "").append(line).append("\n")
                outputBox.setText(outputBuffer.toString())
                outputBox.setCaretPosition(outputBox.getText().length()) // Auto-scroll
            }
            stdoutReader.close()
        })
    }

    /**
     * Displays a confirmation dialog when attempting to close a running process.
     *
     * @param popup The popup window associated with the process.
     * @param process The running process.
     */
    private void showTerminationConfirmation(Window popup, Process process) {
        BasicWindow alertWindow = new BasicWindow("Process Running")

        Panel alertPanel = new Panel(new LinearLayout(Direction.VERTICAL))
        alertPanel.addComponent(new Label("The process is still running. Do you want to terminate it?"))

        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL))
        buttonPanel.addComponent(new Button("OK", { process.destroy(); popup.close(); alertWindow.close() }))
        buttonPanel.addComponent(new Button("Cancel", { alertWindow.close() }))

        alertPanel.addComponent(buttonPanel)
        alertWindow.setComponent(alertPanel)
        alertWindow.setHints([Window.Hint.CENTERED] as Set)

        gui.addWindow(alertWindow)
    }


    /**
     * Replaces the current description with a new one and updates the UI.
     *
     * @param newDescription The new description component.
     */
    private void updateDescription(Description newDescription) {
        mainPanel.removeComponent(mainPanel.children[DESCRIPTION_INDEX])

        this.description = newDescription
        Component newComponent = newDescription.build()
        newComponent.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill, LinearLayout.GrowPolicy.CanGrow))
        mainPanel.addComponent(DESCRIPTION_INDEX, newComponent)

        gui.updateScreen()
    }

    /**
     * Loads the content of a text file from the classpath.
     *
     * @param filePath The relative path of the file inside the classpath.
     * @return The content of the file as a string.
     * @throws IllegalArgumentException If the file cannot be found or read.
     */
    private static String loadTextFromClasspath(String filePath) {
        InputStream inputStream = Page.class.getClassLoader().getResourceAsStream(filePath)
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in classpath: " + filePath)
        }

        try {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read file: " + filePath, e)
        } finally {
            try {
                inputStream.close()
            } catch (IOException ignored) {
            }
        }
    }
}