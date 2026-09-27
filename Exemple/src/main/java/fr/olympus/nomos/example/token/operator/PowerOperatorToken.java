package fr.olympus.nomos.example.token.operator;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericOperatorToken;
import fr.olympus.nomos.language.NumericOperatorType;

@MagicTokenAnnotation(id = "pow", aliases = {"^", "puissance_de"})
public class PowerOperatorToken extends NumericOperatorToken {
    @Override public NumericOperatorType operatorType() { return NumericOperatorType.POWER; }
}
