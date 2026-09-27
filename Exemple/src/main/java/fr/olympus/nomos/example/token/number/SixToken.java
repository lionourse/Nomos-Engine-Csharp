package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "six", aliases = {"6"})
public class SixToken extends NumericMagicToken {
    @Override public double value() { return 6.0; }
}
