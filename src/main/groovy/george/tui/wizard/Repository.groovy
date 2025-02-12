package george.tui.wizard

import java.util.concurrent.ConcurrentHashMap

/**
 * A global repository to store user input values.
 * This allows persistence across multiple screens.
 */
class Repository {
    private static final Map<String, String> storage = new ConcurrentHashMap<>()

    /**
     * Retrieves the stored value for the given key, or the default value if not set.
     */
    static String get(String key, String defaultValue = "") {
        return storage.getOrDefault(key, defaultValue)
    }

    /**
     * Stores a value in the repository.
     */
    static void set(String key, String value) {
        storage.put(key, value)
    }
}