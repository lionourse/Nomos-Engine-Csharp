package fr.olympus.nomos.language;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Splits text on Unicode whitespace and normalizes words for registry lookup.
 */
public class WhitespaceTokenizer implements Tokenizer {

    @Override
    public List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.strip().split("(?U)\\s+"))
                .map(part -> part.toLowerCase(Locale.ROOT))
                .toList();
    }
}
