package fr.olympus.nomos.language;

import java.util.List;

/**
 * Splits source text into token names.
 *
 * <p>Implementations used by a shared spell compiler must be stateless or
 * thread-safe.</p>
 */
@FunctionalInterface
public interface Tokenizer {

    List<String> tokenize(String text);
}
