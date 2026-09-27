package fr.olympus.nomos.language;

/**
 * A token that contributes an operator to a numeric expression.
 */
public abstract class NumericOperatorToken extends MagicToken {

    public abstract NumericOperatorType operatorType();

    public int priority() {
        NumericOperatorType operator = operatorType();
        if (operator == null) {
            throw new IllegalArgumentException("Operator type cannot be null.");
        }
        return operator.getPriority();
    }
}
