package fr.olympus.nomos.condition;

import java.util.ArrayList;
import java.util.List;

/** Collects validation failures produced before a spell is paid for. */
public class SpellValidationResult {
    private final List<String> errors = new ArrayList<>();

    /** Adds a non-blank validation error. */
    public synchronized void addError(String error) {
        if (error != null && !error.isBlank()) {
            errors.add(error);
        }
    }

    /** Returns whether no condition has rejected the spell. */
    public synchronized boolean isValid() {
        return errors.isEmpty();
    }

    /** Returns an immutable snapshot of the current errors. */
    public synchronized List<String> getErrors() {
        return List.copyOf(errors);
    }
}
