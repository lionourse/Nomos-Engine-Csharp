package fr.olympus.nomos.condition;

import fr.olympus.nomos.action.SpellAction;
import fr.olympus.nomos.cast.SpellCaster;
import fr.olympus.nomos.semantic.SpellVector;

/**
 * Validates a spell before resource consumption.
 *
 * <p>The vector-based method preserves the original functional-interface API.
 * Implementations needing caster or action state may override the contextual
 * method instead.</p>
 */
@FunctionalInterface
public interface SpellCondition {
    void validate(SpellVector vector, SpellValidationResult result);

    /** Validates with the complete casting context. */
    default void validate(SpellCaster caster, SpellAction action, SpellValidationResult result) {
        validate(action.getVector(), result);
    }

    /** Creates a condition from a validator that needs the complete cast context. */
    static SpellCondition contextual(ContextualValidator validator) {
        java.util.Objects.requireNonNull(validator, "validator");
        return new SpellCondition() {
            @Override
            public void validate(SpellVector vector, SpellValidationResult result) {
                // Contextual validation is performed by the overload below.
            }

            @Override
            public void validate(SpellCaster caster, SpellAction action, SpellValidationResult result) {
                validator.validate(caster, action, result);
            }
        };
    }

    /** Functional contract used by {@link #contextual(ContextualValidator)}. */
    @FunctionalInterface
    interface ContextualValidator {
        void validate(SpellCaster caster, SpellAction action, SpellValidationResult result);
    }
}
