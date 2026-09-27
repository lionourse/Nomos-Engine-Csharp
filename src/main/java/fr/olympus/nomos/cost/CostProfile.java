package fr.olympus.nomos.cost;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Accumulates base costs and multiplicative modifiers by resource. */
public class CostProfile {
    private final Map<String, Double> addedCosts = new LinkedHashMap<>();
    private final Map<String, Double> multipliers = new LinkedHashMap<>();

    public synchronized void addCost(String resource, double amount) {
        if (!valid(resource, amount)) {
            return;
        }
        String key = normalize(resource);
        double result = addedCosts.getOrDefault(key, 0.0) + Math.max(0.0, amount);
        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("Cost overflow for resource: " + key);
        }
        addedCosts.put(key, result);
    }

    public synchronized void multiplyCost(String resource, double multiplier) {
        if (!valid(resource, multiplier)) {
            return;
        }
        String key = normalize(resource);
        double result = multipliers.getOrDefault(key, 1.0) * Math.max(0.0, multiplier);
        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("Cost multiplier overflow for resource: " + key);
        }
        multipliers.put(key, result);
    }

    public synchronized double getAddedCost(String resource) {
        return resource == null ? 0.0 : addedCosts.getOrDefault(normalize(resource), 0.0);
    }

    public synchronized double getMultiplier(String resource) {
        return resource == null ? 1.0 : multipliers.getOrDefault(normalize(resource), 1.0);
    }

    /** Calculates a stable cost snapshot from one synchronized profile state. */
    public synchronized SpellCost calculateFinalCost() {
        SpellCost finalCost = new SpellCost();
        for (Map.Entry<String, Double> entry : addedCosts.entrySet()) {
            double result = entry.getValue() * multipliers.getOrDefault(entry.getKey(), 1.0);
            if (!Double.isFinite(result)) {
                throw new IllegalStateException("Final cost overflow for resource: " + entry.getKey());
            }
            finalCost.add(entry.getKey(), result);
        }
        return finalCost;
    }

    public synchronized Map<String, Double> getAddedCosts() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(addedCosts));
    }

    public synchronized Map<String, Double> getMultipliers() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(multipliers));
    }

    private static boolean valid(String resource, double value) {
        return resource != null && !resource.isBlank() && Double.isFinite(value);
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public synchronized String toString() {
        return "CostProfile{" + "addedCosts=" + addedCosts + ", multipliers=" + multipliers + '}';
    }
}
