package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "zero", aliases = {"0", "nul"})
public class ZeroToken extends NumericMagicToken {
    @Override public double value() { return 0.0; }
}
