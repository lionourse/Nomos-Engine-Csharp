package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "two", aliases = {"2", "deux"})
public class TwoToken extends NumericMagicToken {
    @Override public double value() { return 2.0; }
}
