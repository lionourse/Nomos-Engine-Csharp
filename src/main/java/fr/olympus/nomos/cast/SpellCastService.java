package fr.olympus.nomos.cast;

import fr.olympus.nomos.action.SpellAction;
import fr.olympus.nomos.action.SpellActionCompiler;
import fr.olympus.nomos.condition.SpellCondition;
import fr.olympus.nomos.condition.SpellValidationResult;
import fr.olympus.nomos.cost.SpellCost;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Compiles, validates, and atomically pays for spells. */
public class SpellCastService {
    private final SpellActionCompiler compiler;
    private final CopyOnWriteArrayList<SpellCondition> conditions;

    public SpellCastService(SpellActionCompiler compiler) {
        this(compiler, List.of());
    }

    public SpellCastService(SpellActionCompiler compiler, Collection<? extends SpellCondition> conditions) {
        if (compiler == null) {
            throw new IllegalArgumentException("SpellActionCompiler cannot be null.");
        }
        this.compiler = compiler;
        Objects.requireNonNull(conditions, "conditions");
        this.conditions = new CopyOnWriteArrayList<>();
        for (SpellCondition condition : conditions) {
            this.conditions.add(Objects.requireNonNull(condition, "condition"));
        }
    }

    /** Adds a condition applied to every subsequent cast. */
    public void addCondition(SpellCondition condition) {
        conditions.add(Objects.requireNonNull(condition, "condition"));
    }

    public boolean removeCondition(SpellCondition condition) {
        return conditions.remove(condition);
    }

    public List<SpellCondition> getConditions() {
        return List.copyOf(conditions);
    }

    public SpellCastResult cast(SpellCaster caster, String text) {
        if (caster == null) {
            throw new IllegalArgumentException("SpellCaster cannot be null.");
        }
        SpellAction action = compiler.compile(text);
        SpellCost cost = action.calculateCost();

        SpellValidationResult validation = validate(caster, action);
        if (!validation.isValid()) {
            return SpellCastResult.validationFailure(buildValidationMessage(validation), action, cost, validation);
        }

        if (!caster.getResources().tryConsume(cost)) {
            return SpellCastResult.failure(buildMissingResourceMessage(caster, cost), action, cost);
        }
        return SpellCastResult.success(action, cost);
    }

    private SpellValidationResult validate(SpellCaster caster, SpellAction action) {
        SpellValidationResult result = new SpellValidationResult();
        for (SpellCondition condition : conditions) {
            condition.validate(caster, action, result);
        }
        for (SpellCondition condition : action.getConditions()) {
            condition.validate(caster, action, result);
        }
        return result;
    }

    private String buildValidationMessage(SpellValidationResult result) {
        return "Spell validation failed: " + String.join(", ", result.getErrors());
    }

    private String buildMissingResourceMessage(SpellCaster caster, SpellCost cost) {
        StringBuilder builder = new StringBuilder("Not enough resources: ");
        boolean first = true;
        Map<String, Double> availableResources = caster.getResources().getResources();
        for (Map.Entry<String, Double> entry : cost.getCosts().entrySet()) {
            double available = availableResources.getOrDefault(entry.getKey(), 0.0);
            if (available >= entry.getValue()) {
                continue;
            }
            if (!first) {
                builder.append(", ");
            }
            builder.append(entry.getKey())
                    .append(" required=").append(String.format(Locale.ROOT, "%.2f", entry.getValue()))
                    .append(" available=").append(String.format(Locale.ROOT, "%.2f", available));
            first = false;
        }
        return builder.toString();
    }
}
