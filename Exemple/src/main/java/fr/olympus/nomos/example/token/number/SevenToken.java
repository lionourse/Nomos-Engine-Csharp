package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "seven", aliases = {"7", "sept"})
public class SevenToken extends NumericMagicToken {
    @Override public double value() { return 7.0; }
}
