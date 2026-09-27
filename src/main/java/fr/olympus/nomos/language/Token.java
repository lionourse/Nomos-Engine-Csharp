package fr.olympus.nomos.language;

import fr.olympus.nomos.semantic.MagicMeaning;

import java.util.Locale;

/**
 * Immutable lexical occurrence resolved by the spell compiler.
 */
public final class Token {

    private final String value;
    private final MagicMeaning meaning;

    public Token(String value, MagicMeaning meaning) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Token value cannot be null or blank.");
        }
        if (meaning == null) {
            throw new IllegalArgumentException("Token meaning cannot be null.");
        }
        this.value = value.strip().toLowerCase(Locale.ROOT);
        this.meaning = copyOf(meaning);
    }

    public String getValue() {
        return value;
    }

    public MagicMeaning getMeaning() {
        return copyOf(meaning);
    }

    @Override
    public String toString() {
        return "Token{" +
                "value='" + value + '\'' +
                ", meaning=" + meaning +
                '}';
    }

    private static MagicMeaning copyOf(MagicMeaning source) {
        MagicMeaning copy = new MagicMeaning();
        source.getDimensions().forEach(copy::add);
        return copy;
    }
}
