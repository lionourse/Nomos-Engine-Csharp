package fr.olympus.nomos.semantic;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe semantic dimensions contributed by a magic token.
 */
public class MagicMeaning {

    private final ConcurrentMap<String, Double> dimensions = new ConcurrentHashMap<>();

    public MagicMeaning add(String dimension, double value) {
        String normalized = normalizeRequired(dimension, "Dimension");
        requireFinite(value, "Dimension value");
        dimensions.put(normalized, value);
        return this;
    }

    /**
     * Returns an immutable point-in-time snapshot ordered by dimension.
     */
    public Map<String, Double> getDimensions() {
        return Collections.unmodifiableMap(new TreeMap<>(dimensions));
    }

    private static String normalizeRequired(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be null or blank.");
        }
        return value.strip().toLowerCase(Locale.ROOT);
    }

    private static void requireFinite(double value, String label) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(label + " must be finite: " + value);
        }
    }
}
