package fr.olympus.nomos.math;

/**
 * Result of attempting to parse and evaluate a numeric expression.
 */
public class NumericExpressionResult {

    private final boolean valid;
    private final double value;
    private final int consumedTokens;
    private final String error;

    private NumericExpressionResult(boolean valid, double value, int consumedTokens, String error) {
        this.valid = valid;
        this.value = value;
        this.consumedTokens = consumedTokens;
        this.error = error;
    }

    public static NumericExpressionResult success(double value, int consumedTokens) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Expression result must be finite: " + value);
        }
        if (consumedTokens <= 0) {
            throw new IllegalArgumentException("Consumed token count must be positive.");
        }
        return new NumericExpressionResult(true, value, consumedTokens, null);
    }

    public static NumericExpressionResult failure(String error) {
        if (error == null || error.isBlank()) {
            throw new IllegalArgumentException("Expression error cannot be null or blank.");
        }
        return new NumericExpressionResult(false, 0.0D, 0, error);
    }

    public boolean isValid() {
        return valid;
    }

    public double getValue() {
        return value;
    }

    public int getConsumedTokens() {
        return consumedTokens;
    }

    public String getError() {
        return error;
    }
}
