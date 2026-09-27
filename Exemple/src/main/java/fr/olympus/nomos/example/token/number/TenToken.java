package fr.olympus.nomos.example.token.number;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericMagicToken;

@MagicTokenAnnotation(id = "ten", aliases = {"10", "dix"})
public class TenToken extends NumericMagicToken {
    @Override public double value() { return 10.0; }
}
