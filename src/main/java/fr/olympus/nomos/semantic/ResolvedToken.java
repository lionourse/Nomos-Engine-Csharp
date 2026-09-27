package fr.olympus.nomos.semantic;

import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.Token;

import java.util.Objects;

/**
 * A recognized lexical occurrence associated with its registered definition.
 *
 * @param sourceIndex zero-based position in the tokenizer output
 * @param definition registered singleton definition for the occurrence
 * @param occurrence immutable lexical occurrence exposed in the compiled trace
 */
public record ResolvedToken(
        int sourceIndex,
        MagicToken definition,
        Token occurrence
) {

    /**
     * Creates a validated resolved occurrence.
     *
     * @throws IllegalArgumentException when {@code sourceIndex} is negative
     * @throws NullPointerException when a required reference is {@code null}
     */
    public ResolvedToken {
        if (sourceIndex < 0) {
            throw new IllegalArgumentException("Source index cannot be negative.");
        }
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(occurrence, "occurrence");
    }
}
