package fr.olympus.nomos.semantic;

/**
 * Analyzes resolved source tokens and defines the order in which the compiler
 * must evaluate them.
 *
 * <p>A strategy does not tokenize text, resolve token names, or apply action
 * contributions. It receives an immutable compilation context and returns a
 * semantic plan consumed by the spell compiler. Implementations shared by
 * multiple compiler instances or concurrent compilations must be stateless or
 * thread-safe.</p>
 */
@FunctionalInterface
public interface SemanticStrategy {

    /**
     * Produces the semantic evaluation plan for one compilation.
     *
     * @param context immutable resolved-token context
     * @return the semantic evaluation plan, never {@code null}
     * @throws SemanticAnalysisException when the input does not satisfy the
     *         strategy's grammar
     */
    SemanticPlan analyze(SemanticContext context);
}
