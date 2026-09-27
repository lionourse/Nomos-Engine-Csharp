package fr.olympus.nomos.action;

import fr.olympus.nomos.condition.SpellCondition;
import fr.olympus.nomos.cost.CostProfile;
import fr.olympus.nomos.cost.SpellCost;
import fr.olympus.nomos.language.Token;
import fr.olympus.nomos.semantic.SpellVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** The structured result produced by compiling a spell phrase. */
public class SpellAction {
    private final SpellVector vector;
    private final List<Token> tokens;
    private final CostProfile costProfile = new CostProfile();
    private final Map<String, Object> properties = new LinkedHashMap<>();
    private final Map<String, Double> numericProperties = new LinkedHashMap<>();
    private final List<SpellCondition> conditions = new ArrayList<>();
    private SpellActionType actionType = SpellActionType.UNKNOWN;

    public SpellAction(SpellVector vector) {
        this(vector, List.of());
    }

    /** Creates an action with an immutable trace of recognized source tokens. */
    public SpellAction(SpellVector vector, List<? extends Token> tokens) {
        this.vector = Objects.requireNonNull(vector, "vector");
        this.tokens = List.copyOf(Objects.requireNonNull(tokens, "tokens"));
    }

    public SpellVector getVector() { return vector; }
    public List<Token> getTokens() { return tokens; }
    public CostProfile getCostProfile() { return costProfile; }
    public SpellCost calculateCost() { return costProfile.calculateFinalCost(); }

    public synchronized void setProperty(String key, Object value) {
        if (key == null || key.isBlank()) {
            return;
        }
        String normalized = normalize(key);
        if ("action_type".equals(normalized)) {
            actionType = SpellActionType.fromProperty(value);
            properties.put(normalized, value instanceof SpellActionType type ? type.propertyValue() : value);
            return;
        }
        properties.put(normalized, value);
    }

    /** Updates both the typed action kind and its legacy string property. */
    public synchronized void setActionType(SpellActionType type) {
        actionType = Objects.requireNonNull(type, "type");
        properties.put("action_type", type.propertyValue());
    }

    public synchronized SpellActionType getActionType() {
        return actionType;
    }

    public synchronized void addNumericProperty(String key, double value) {
        if (key == null || key.isBlank() || !Double.isFinite(value)) {
            return;
        }
        String normalized = normalize(key);
        double result = numericProperties.getOrDefault(normalized, 0.0) + value;
        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("Numeric property overflow: " + normalized);
        }
        numericProperties.put(normalized, result);
    }

    public synchronized void setNumericProperty(String key, double value) {
        if (key == null || key.isBlank() || !Double.isFinite(value)) {
            return;
        }
        numericProperties.put(normalize(key), value);
    }

    public synchronized void addCondition(SpellCondition condition) {
        conditions.add(Objects.requireNonNull(condition, "condition"));
    }

    public synchronized List<SpellCondition> getConditions() {
        return List.copyOf(conditions);
    }

    public synchronized Object getProperty(String key) {
        return key == null ? null : properties.get(normalize(key));
    }

    public synchronized String getStringProperty(String key, String defaultValue) {
        Object value = key == null ? null : properties.get(normalize(key));
        return value == null ? defaultValue : String.valueOf(value);
    }

    public synchronized boolean getBooleanProperty(String key, boolean defaultValue) {
        Object value = key == null ? null : properties.get(normalize(key));
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return value == null ? defaultValue : Boolean.parseBoolean(String.valueOf(value));
    }

    public synchronized double getNumericProperty(String key) {
        return key == null ? 0.0 : numericProperties.getOrDefault(normalize(key), 0.0);
    }

    /** Returns an immutable point-in-time property snapshot. */
    public synchronized Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(properties));
    }

    /** Returns an immutable point-in-time numeric-property snapshot. */
    public synchronized Map<String, Double> getNumericProperties() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(numericProperties));
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public synchronized String toString() {
        return "SpellAction{" + "type=" + actionType + ", properties=" + properties
                + ", numericProperties=" + numericProperties + ", cost=" + calculateCost()
                + ", vector=" + vector.getValues() + '}';
    }
}
