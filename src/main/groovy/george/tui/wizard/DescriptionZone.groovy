package george.tui.wizard

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.TextColor

/**
 * Displays a description in the top zone.
 * Background is magenta, text is black, and the panel has a white border.
 */
class DescriptionZone {
    private final String text

    DescriptionZone(String text) {
        this.text = text
    }

    Component build() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        panel.setFillColorOverride(TextColor.ANSI.CYAN)  // Updated line

        Label label = new Label(text)
        label.setForegroundColor(TextColor.ANSI.BLACK)
        label.setBackgroundColor(TextColor.ANSI.CYAN)
        panel.addComponent(label)

        return panel
    }
}