package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "one", aliases = {"1", "un"})
public class OneToken extends NumericMagicToken {
    @Override public double value() { return 1.0; }
}
