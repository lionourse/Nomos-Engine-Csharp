package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "four", aliases = {"4", "quatre"})
public class FourToken extends NumericMagicToken {
    @Override public double value() { return 4.0; }
}
