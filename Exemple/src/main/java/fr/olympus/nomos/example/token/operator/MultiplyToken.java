package fr.olympus.nomos.example.token.operator;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericOperatorToken;
import fr.olympus.nomos.language.NumericOperatorType;

@MagicTokenAnnotation(id = "multiply", aliases = {"*", "fois", "multiplier"})
public class MultiplyToken extends NumericOperatorToken {
    @Override public NumericOperatorType operatorType() { return NumericOperatorType.MULTIPLY; }
}
