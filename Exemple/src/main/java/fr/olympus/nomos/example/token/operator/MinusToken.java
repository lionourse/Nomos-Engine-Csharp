package fr.olympus.nomos.example.token.operator;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.NumericOperatorToken;
import fr.olympus.nomos.language.NumericOperatorType;

@MagicTokenAnnotation(id = "minus", aliases = {"-", "moins", "soustraction"})
public class MinusToken extends NumericOperatorToken {
    @Override public NumericOperatorType operatorType() { return NumericOperatorType.SUBTRACT; }
}
