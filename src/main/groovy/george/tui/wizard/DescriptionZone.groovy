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
        // Approximately top 2/3 of an 18-row screen (e.g. 12 rows):
        panel.setPreferredSize(new TerminalSize(40, 12))
        panel.setFillColorOverride(TextColor.ANSI.MAGENTA)  // Updated line
        panel.withBorder(Borders.singleLine(" Description "))

        Label label = new Label(text)
        label.setForegroundColor(TextColor.ANSI.BLACK)
        panel.addComponent(label)

        return panel
    }
}