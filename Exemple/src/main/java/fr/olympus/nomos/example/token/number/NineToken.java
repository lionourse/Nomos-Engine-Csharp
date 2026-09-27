package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "nine", aliases = {"9", "neuf"})
public class NineToken extends NumericMagicToken {
    @Override public double value() { return 9.0; }
}
