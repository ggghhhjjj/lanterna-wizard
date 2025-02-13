package george.tui.wizard

import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.TextColor
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Displays a description in the top zone.
 * Supports dynamic placeholder replacement with Repository values.
 */
class DescriptionZone {
    private String text
    private final Set<String> usedVariables = new HashSet<>()
    private Label label

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile('\\$\\{([a-zA-Z0-9_.]+)}')

    DescriptionZone(String text) {
        this.text = text
        extractUsedVariables()
        Repository.addListener(this::onRepositoryUpdate) // Subscribe to repository updates
    }

    Component build() {
        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL))
        panel.setFillColorOverride(TextColor.ANSI.CYAN)

        label = new Label(resolvePlaceholders(text))
        label.setForegroundColor(TextColor.ANSI.BLACK)
        label.setBackgroundColor(TextColor.ANSI.CYAN)
        panel.addComponent(label)

        return panel
    }

    /** Extracts and stores variable names from placeholders */
    private void extractUsedVariables() {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text)
        while (matcher.find()) {
            usedVariables.add(matcher.group(1))
        }
    }

    /** Replaces placeholders in text with values from Repository */
    private static String resolvePlaceholders(String input) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input)
        StringBuffer result = new StringBuffer()

        while (matcher.find()) {
            String variable = matcher.group(1)
            String value = Repository.get(variable) ?: "" // Default to empty string if null
            matcher.appendReplacement(result, Matcher.quoteReplacement(value))
        }
        matcher.appendTail(result)

        return result.toString()
    }

    /** Updates the label when a tracked variable is changed in Repository */
    private void onRepositoryUpdate(String key) {
        if (usedVariables.contains(key)) {
            label.setText(resolvePlaceholders(text))
        }
    }
}