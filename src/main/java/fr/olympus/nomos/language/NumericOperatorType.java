package fr.olympus.nomos.language;

/**
 * Operators supported by Nomos numeric expressions.
 */
public enum NumericOperatorType {

    ADD(1),
    SUBTRACT(1),
    MULTIPLY(2),
    DIVIDE(2),
    MODULO(2),
    POWER(3);

    private static final double ZERO_EPSILON = 0.0000001D;

    private final int priority;

    NumericOperatorType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public double apply(double left, double right) {
        requireFinite(left, "Left operand");
        requireFinite(right, "Right operand");

        double result = switch (this) {
            case ADD -> left + right;
            case SUBTRACT -> left - right;
            case MULTIPLY -> left * right;
            case DIVIDE -> {
                requireNonZero(right, "divide");
                yield left / right;
            }
            case MODULO -> {
                requireNonZero(right, "modulo");
                yield left % right;
            }
            case POWER -> Math.pow(left, right);
        };

        if (!Double.isFinite(result)) {
            throw new ArithmeticException("Numeric operation produced a non-finite result: " + result);
        }
        return result;
    }

    private static void requireFinite(double value, String name) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be finite: " + value);
        }
    }

    private static void requireNonZero(double value, String operation) {
        if (Math.abs(value) <= ZERO_EPSILON) {
            throw new ArithmeticException("Cannot " + operation + " by zero.");
        }
    }
}
