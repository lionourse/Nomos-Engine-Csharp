package fr.olympus.nomos.cost;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Represents the non-negative resource cost of a compiled spell. */
public class SpellCost {
    private final Map<String, Double> costs = new LinkedHashMap<>();

    public synchronized void add(String resource, double amount) {
        if (resource == null || resource.isBlank() || !Double.isFinite(amount)) {
            return;
        }
        String key = normalize(resource);
        double result = costs.getOrDefault(key, 0.0) + Math.max(0.0, amount);
        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("Cost overflow for resource: " + key);
        }
        costs.put(key, result);
    }

    public synchronized double get(String resource) {
        return resource == null ? 0.0 : costs.getOrDefault(normalize(resource), 0.0);
    }

    public synchronized boolean isEmpty() {
        return costs.isEmpty();
    }

    /** Returns an immutable snapshot, safe to retain across concurrent updates. */
    public synchronized Map<String, Double> getCosts() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(costs));
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public synchronized String toString() {
        return costs.toString();
    }
}
