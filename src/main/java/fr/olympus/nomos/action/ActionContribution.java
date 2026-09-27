package fr.olympus.nomos.action;

import fr.olympus.nomos.condition.SpellCondition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** A mutable builder describing the changes contributed by one magic token. */
public class ActionContribution {
    private final Map<String, Object> properties = new LinkedHashMap<>();
    private final Map<String, Double> addedNumbers = new LinkedHashMap<>();
    private final Map<String, Double> setNumbers = new LinkedHashMap<>();
    private final Map<String, Double> addedCosts = new LinkedHashMap<>();
    private final Map<String, Double> costMultipliers = new LinkedHashMap<>();
    private final List<SpellCondition> conditions = new ArrayList<>();

    public synchronized ActionContribution set(String key, Object value) {
        String normalized = requireKey(key);
        if ("action_type".equals(normalized) && value instanceof SpellActionType type) {
            value = type.propertyValue();
        }
        properties.put(normalized, value);
        return this;
    }

    /** Sets the typed action kind while retaining the legacy string property. */
    public ActionContribution setActionType(SpellActionType type) {
        return set("action_type", Objects.requireNonNull(type, "type"));
    }

    public synchronized ActionContribution addNumber(String key, double value) {
        String normalized = requireKey(key);
        requireFinite(value);
        double result = addedNumbers.getOrDefault(normalized, 0.0) + value;
        requireFinite(result);
        addedNumbers.put(normalized, result);
        return this;
    }

    public synchronized ActionContribution setNumber(String key, double value) {
        String normalized = requireKey(key);
        requireFinite(value);
        setNumbers.put(normalized, value);
        return this;
    }

    public synchronized ActionContribution addCost(String resource, double amount) {
        String normalized = requireKey(resource);
        requireFinite(amount);
        double result = addedCosts.getOrDefault(normalized, 0.0) + Math.max(0.0, amount);
        requireFinite(result);
        addedCosts.put(normalized, result);
        return this;
    }

    public synchronized ActionContribution multiplyCost(String resource, double multiplier) {
        String normalized = requireKey(resource);
        requireFinite(multiplier);
        double result = costMultipliers.getOrDefault(normalized, 1.0) * Math.max(0.0, multiplier);
        requireFinite(result);
        costMultipliers.put(normalized, result);
        return this;
    }

    /** Adds a condition evaluated by {@code SpellCastService} before payment. */
    public synchronized ActionContribution addCondition(SpellCondition condition) {
        conditions.add(Objects.requireNonNull(condition, "condition"));
        return this;
    }

    public synchronized Map<String, Object> getProperties() {
        return java.util.Collections.unmodifiableMap(new LinkedHashMap<>(properties));
    }
    public synchronized Map<String, Double> getAddedNumbers() { return Map.copyOf(addedNumbers); }
    public synchronized Map<String, Double> getSetNumbers() { return Map.copyOf(setNumbers); }
    public synchronized Map<String, Double> getAddedCosts() { return Map.copyOf(addedCosts); }
    public synchronized Map<String, Double> getCostMultipliers() { return Map.copyOf(costMultipliers); }
    public synchronized List<SpellCondition> getConditions() { return List.copyOf(conditions); }

    private static String requireKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or empty.");
        }
        return key.trim().toLowerCase(Locale.ROOT);
    }

    private static void requireFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid numeric value: " + value);
        }
    }
}
