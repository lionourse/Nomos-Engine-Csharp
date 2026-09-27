package fr.olympus.nomos.cost;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Thread-safe storage for a caster's named resources. */
public class ResourceContainer {
    private final Map<String, Double> resources = new LinkedHashMap<>();

    public synchronized void set(String resource, double amount) {
        if (!valid(resource, amount)) {
            return;
        }
        resources.put(normalize(resource), Math.max(0.0, amount));
    }

    public synchronized void add(String resource, double amount) {
        if (!valid(resource, amount)) {
            return;
        }
        String key = normalize(resource);
        double result = Math.max(0.0, resources.getOrDefault(key, 0.0) + amount);
        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("Resource overflow: " + key);
        }
        resources.put(key, result);
    }

    public synchronized boolean consume(String resource, double amount) {
        if (!valid(resource, amount) || amount < 0.0) {
            return false;
        }
        String key = normalize(resource);
        double current = resources.getOrDefault(key, 0.0);
        if (current < amount) {
            return false;
        }
        resources.put(key, current - amount);
        return true;
    }

    public synchronized boolean canAfford(SpellCost cost) {
        if (cost == null) {
            return true;
        }
        return canAfford(cost.getCosts());
    }

    private boolean canAfford(Map<String, Double> costs) {
        for (Map.Entry<String, Double> entry : costs.entrySet()) {
            if (resources.getOrDefault(entry.getKey(), 0.0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Atomically verifies and consumes an entire cost. No resource is changed
     * when any required amount is unavailable.
     */
    public synchronized boolean tryConsume(SpellCost cost) {
        Map<String, Double> costs = cost == null ? Map.of() : cost.getCosts();
        if (!canAfford(costs)) {
            return false;
        }
        for (Map.Entry<String, Double> entry : costs.entrySet()) {
            String key = entry.getKey();
            resources.put(key, resources.getOrDefault(key, 0.0) - entry.getValue());
        }
        return true;
    }

    /** Preserves the historical throwing API while performing one atomic payment. */
    public void consume(SpellCost cost) {
        if (!tryConsume(cost)) {
            throw new IllegalStateException("Cannot afford spell cost: " + cost);
        }
    }

    public synchronized double get(String resource) {
        return resource == null ? 0.0 : resources.getOrDefault(normalize(resource), 0.0);
    }

    /** Returns an immutable point-in-time resource snapshot. */
    public synchronized Map<String, Double> getResources() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(resources));
    }

    private static boolean valid(String resource, double amount) {
        return resource != null && !resource.isBlank() && Double.isFinite(amount);
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public synchronized String toString() {
        return resources.toString();
    }
}
