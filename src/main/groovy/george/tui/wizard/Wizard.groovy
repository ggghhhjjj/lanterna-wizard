package george.tui.wizard

import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory

/**
 * The Wizard class manages a sequence of pages displayed on a Lanterna-based
 * text user interface (TUI). It initializes and controls the lifecycle of the screen,
 * handles navigation among pages, and provides methods for page management.*/
class Wizard {

    /**
     * The Lanterna Screen instance used to render the wizard UI.*/
    private final Screen screen

    /**
     * A list containing all pages added to the wizard.*/
    private final LinkedList<Page> windows = new LinkedList<>()

    /**
     * Constructs a new Wizard instance and initializes the screen.*/
    Wizard() {
        this.screen = new DefaultTerminalFactory().createScreen()
        screen.startScreen()
    }

    /**
     * Adds a new page with the given title to the wizard.
     *
     * @param title the title of the page to be created
     * @return the created {@link Page} instance
     */
    Page addPage(String title) {
        Page page = new Page(title, screen)
        windows.add(page)
        return page
    }

    /**
     * Stops the wizard and releases screen resources.*/
    void stop() {
        screen.stopScreen()
    }
}