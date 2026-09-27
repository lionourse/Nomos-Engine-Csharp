package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "eight", aliases = {"8", "huit"})
public class EightToken extends NumericMagicToken {
    @Override public double value() { return 8.0; }
}
