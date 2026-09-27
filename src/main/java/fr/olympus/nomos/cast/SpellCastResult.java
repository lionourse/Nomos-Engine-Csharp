package fr.olympus.nomos.cast;

import fr.olympus.nomos.action.SpellAction;
import fr.olympus.nomos.condition.SpellValidationResult;
import fr.olympus.nomos.cost.SpellCost;

import java.util.Objects;

/** Outcome of compiling, validating, and paying for a spell. */
public class SpellCastResult {
    private final boolean success;
    private final String message;
    private final SpellAction action;
    private final SpellCost cost;
    private final SpellValidationResult validationResult;

    private SpellCastResult(boolean success, String message, SpellAction action, SpellCost cost,
                            SpellValidationResult validationResult) {
        this.success = success;
        this.message = Objects.requireNonNullElse(message, "");
        this.action = Objects.requireNonNull(action, "action");
        this.cost = Objects.requireNonNull(cost, "cost");
        this.validationResult = Objects.requireNonNull(validationResult, "validationResult");
    }

    public static SpellCastResult success(SpellAction action, SpellCost cost) {
        return new SpellCastResult(true, "Spell cast successfully.", action, cost, new SpellValidationResult());
    }

    public static SpellCastResult failure(String message, SpellAction action, SpellCost cost) {
        return new SpellCastResult(false, message, action, cost, new SpellValidationResult());
    }

    /** Creates a failure that exposes the conditions which rejected the spell. */
    public static SpellCastResult validationFailure(String message, SpellAction action, SpellCost cost,
                                                    SpellValidationResult validationResult) {
        return new SpellCastResult(false, message, action, cost, validationResult);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public SpellAction getAction() { return action; }
    public SpellCost getCost() { return cost; }
    public SpellValidationResult getValidationResult() { return validationResult; }

    @Override
    public String toString() {
        return "SpellCastResult{" + "success=" + success + ", message='" + message + '\''
                + ", cost=" + cost + ", action=" + action + '}';
    }
}
