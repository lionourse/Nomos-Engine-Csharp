package fr.olympus.nomos.language;

/**
 * A token that contributes a finite numeric value to an expression.
 */
public abstract class NumericMagicToken extends MagicToken {

    public abstract double value();

    @Override
    public final boolean isNumeric() {
        return true;
    }
}
