package fr.olympus.nomos.math;

import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.NumericMagicToken;
import fr.olympus.nomos.language.NumericOperatorToken;
import fr.olympus.nomos.language.NumericOperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateless and thread-safe parser for numeric token sequences.
 */
public class NumericExpressionParser {

    public NumericExpressionResult parse(List<MagicToken> tokens, int startIndex) {
        if (tokens == null || tokens.isEmpty()) {
            return NumericExpressionResult.failure("Token list is empty.");
        }

        List<MagicToken> snapshot = new ArrayList<>(tokens);
        if (startIndex < 0 || startIndex >= snapshot.size()) {
            return NumericExpressionResult.failure("Invalid expression start index.");
        }

        NumericExpression expression = new NumericExpression();
        int index = startIndex;
        boolean expectingNumber = true;
        int consumed = 0;

        while (index < snapshot.size()) {
            MagicToken token = snapshot.get(index);
            if (expectingNumber) {
                if (!(token instanceof NumericMagicToken numberToken)) {
                    break;
                }

                double value = numberToken.value();
                if (!Double.isFinite(value)) {
                    return NumericExpressionResult.failure("Numeric token value must be finite.");
                }
                expression.addValue(value);
                expectingNumber = false;
            } else {
                if (!(token instanceof NumericOperatorToken operatorToken)) {
                    break;
                }

                NumericOperatorType operator = operatorToken.operatorType();
                if (operator == null) {
                    return NumericExpressionResult.failure("Numeric operator type cannot be null.");
                }
                expression.addOperator(operator);
                expectingNumber = true;
            }
            consumed++;
            index++;
        }

        if (consumed == 0) {
            return NumericExpressionResult.failure("No numeric expression found.");
        }
        if (!expression.isComplete()) {
            return NumericExpressionResult.failure("Incomplete numeric expression.");
        }

        try {
            return NumericExpressionResult.success(expression.evaluate(), consumed);
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            return NumericExpressionResult.failure(
                    message == null || message.isBlank() ? "Could not evaluate numeric expression." : message
            );
        }
    }
}
