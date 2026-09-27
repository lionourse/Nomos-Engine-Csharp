package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "three", aliases = {"3", "trois"})
public class ThreeToken extends NumericMagicToken {
    @Override public double value() { return 3.0; }
}
