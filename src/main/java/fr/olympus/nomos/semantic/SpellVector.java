package fr.olympus.nomos.semantic;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe accumulated semantic vector for a compiled spell.
 */
public class SpellVector {

    private final ConcurrentMap<String, Double> values = new ConcurrentHashMap<>();

    public void add(String dimension, double amount) {
        String normalized = normalizeRequired(dimension, "Dimension");
        requireFinite(amount, "Dimension amount");

        values.compute(normalized, (ignored, current) -> {
            double result = (current == null ? 0.0D : current) + amount;
            if (!Double.isFinite(result)) {
                throw new ArithmeticException("Dimension value overflow: " + normalized);
            }
            return result;
        });
    }

    public void merge(MagicMeaning meaning) {
        if (meaning == null) {
            throw new IllegalArgumentException("Magic meaning cannot be null.");
        }
        meaning.getDimensions().forEach(this::add);
    }

    public double get(String dimension) {
        String normalized = normalizeRequired(dimension, "Dimension");
        return values.getOrDefault(normalized, 0.0D);
    }

    /**
     * Returns an immutable point-in-time snapshot ordered by dimension.
     */
    public Map<String, Double> getValues() {
        return Collections.unmodifiableMap(new TreeMap<>(values));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        getValues().forEach((dimension, value) -> builder
                .append(dimension)
                .append(" = ")
                .append(String.format(Locale.ROOT, "%.3f", value))
                .append('\n'));
        return builder.toString();
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
