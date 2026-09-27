package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "five", aliases = {"5", "cinq"})
public class FiveToken extends NumericMagicToken {
    @Override public double value() { return 5.0; }
}
