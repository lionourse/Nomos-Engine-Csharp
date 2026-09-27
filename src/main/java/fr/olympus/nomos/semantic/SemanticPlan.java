package fr.olympus.nomos.semantic;

import java.util.List;
import java.util.Objects;

/**
 * Immutable order in which resolved tokens must be evaluated by the compiler.
 *
 * <p>A valid plan is expected to contain each occurrence from its semantic
 * context exactly once. The compiler is responsible for enforcing that
 * permutation constraint before applying contributions.</p>
 *
 * @param evaluationOrder resolved occurrences in semantic evaluation order
 */
public record SemanticPlan(List<ResolvedToken> evaluationOrder) {

    /**
     * Creates a plan with an immutable defensive copy of its evaluation order.
     *
     * @throws NullPointerException when the list or one of its elements is
     *         {@code null}
     */
    public SemanticPlan {
        evaluationOrder = List.copyOf(Objects.requireNonNull(evaluationOrder, "evaluationOrder"));
    }

    /**
     * Creates a plan that preserves the source order of the supplied context.
     *
     * @param context semantic context to preserve
     * @return a sequential semantic plan
     * @throws NullPointerException when {@code context} is {@code null}
     */
    public static SemanticPlan sequential(SemanticContext context) {
        return new SemanticPlan(Objects.requireNonNull(context, "context").sourceTokens());
    }
}
