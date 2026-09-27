package fr.olympus.nomos.math;

import fr.olympus.nomos.language.NumericOperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable numeric expression evaluated with Nomos operator priorities.
 */
public class NumericExpression {

    private final List<Double> values = new ArrayList<>();
    private final List<NumericOperatorType> operators = new ArrayList<>();

    public synchronized void addValue(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Numeric value must be finite: " + value);
        }
        values.add(value);
    }

    public synchronized void addOperator(NumericOperatorType operator) {
        if (operator == null) {
            throw new IllegalArgumentException("Operator cannot be null.");
        }
        operators.add(operator);
    }

    public synchronized boolean isEmpty() {
        return values.isEmpty();
    }

    public synchronized boolean isComplete() {
        return !values.isEmpty() && values.size() == operators.size() + 1;
    }

    public synchronized double evaluate() {
        if (!isComplete()) {
            throw new IllegalStateException("Invalid numeric expression.");
        }

        List<Double> workingValues = new ArrayList<>(values);
        List<NumericOperatorType> workingOperators = new ArrayList<>(operators);

        applyPriority(workingValues, workingOperators, NumericOperatorType.POWER.getPriority());
        applyPriority(workingValues, workingOperators, NumericOperatorType.MULTIPLY.getPriority());
        applyPriority(workingValues, workingOperators, NumericOperatorType.ADD.getPriority());

        if (workingValues.size() != 1) {
            throw new IllegalStateException("Could not reduce numeric expression.");
        }
        return workingValues.getFirst();
    }

    private static void applyPriority(
            List<Double> workingValues,
            List<NumericOperatorType> workingOperators,
            int priority
    ) {
        int index = 0;
        while (index < workingOperators.size()) {
            NumericOperatorType operator = workingOperators.get(index);
            if (operator.getPriority() != priority) {
                index++;
                continue;
            }

            double result = operator.apply(workingValues.get(index), workingValues.get(index + 1));
            workingValues.set(index, result);
            workingValues.remove(index + 1);
            workingOperators.remove(index);
        }
    }
}
