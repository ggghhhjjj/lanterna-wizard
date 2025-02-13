package george.tui.wizard

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A global repository to store user input values.
 * Supports listeners that notify subscribers when a value changes.
 */
class Repository {
    private static final Map<String, String> storage = new ConcurrentHashMap<>()
    private static final List<RepositoryListener> listeners = new CopyOnWriteArrayList<>()

    /**
     * Retrieves the stored value for the given key, or the default value if not set.
     */
    static String get(String key, String defaultValue = "") {
        return storage.getOrDefault(key, defaultValue)
    }

    /**
     * Stores a value in the repository and notifies listeners if the value changes.
     */
    static void set(String key, String value) {
        if (!storage.containsKey(key) || !storage.get(key).equals(value)) {
            storage.put(key, value)
            notifyListeners(key)
        }
    }

    /**
     * Adds a listener that gets notified when a value changes.
     */
    static void addListener(RepositoryListener listener) {
        listeners.add(listener)
    }

    /**
     * Notifies all registered listeners about a change in the given key.
     */
    private static void notifyListeners(String key) {
        listeners.each { it.onRepositoryUpdate(key) }
    }

    /**
     * Listener interface for repository updates.
     */
    interface RepositoryListener {
        void onRepositoryUpdate(String key)
    }
}