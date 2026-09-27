package fr.olympus.nomos.semantic;

import java.util.Objects;

/**
 * Default stateless semantic strategy preserving the recognized source order.
 *
 * <p>The singleton is immutable and safe for concurrent use.</p>
 */
public final class SequentialSemanticStrategy implements SemanticStrategy {

    /** Shared thread-safe sequential strategy instance. */
    public static final SequentialSemanticStrategy INSTANCE = new SequentialSemanticStrategy();

    private SequentialSemanticStrategy() {
    }

    /**
     * Returns a plan whose evaluation order is identical to the source order.
     *
     * @param context immutable resolved-token context
     * @return a sequential semantic plan
     * @throws NullPointerException when {@code context} is {@code null}
     */
    @Override
    public SemanticPlan analyze(SemanticContext context) {
        return SemanticPlan.sequential(Objects.requireNonNull(context, "context"));
    }
}
