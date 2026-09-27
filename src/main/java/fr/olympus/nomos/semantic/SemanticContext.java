package fr.olympus.nomos.semantic;

import java.util.List;
import java.util.Objects;

/**
 * Immutable input supplied to a {@link SemanticStrategy} for one compilation.
 *
 * <p>The token list follows source order. Its elements refer to the registered
 * token definition singletons, which must themselves be stateless or
 * thread-safe.</p>
 *
 * @param sourceTokens recognized token occurrences in source order
 */
public record SemanticContext(List<ResolvedToken> sourceTokens) {

    /**
     * Creates a context with an immutable defensive copy of its token list.
     *
     * @throws NullPointerException when the list or one of its elements is
     *         {@code null}
     */
    public SemanticContext {
        sourceTokens = List.copyOf(Objects.requireNonNull(sourceTokens, "sourceTokens"));
    }
}
