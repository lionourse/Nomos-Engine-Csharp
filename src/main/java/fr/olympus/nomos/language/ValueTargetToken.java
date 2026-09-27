package fr.olympus.nomos.language;

import fr.olympus.nomos.action.ActionContribution;

/**
 * A token whose action may be configured by the numeric expression following it.
 */
public abstract class ValueTargetToken extends MagicToken {

    @Override
    public final boolean acceptsValue() {
        return true;
    }

    public abstract ActionContribution actionWithValue(double value);
}
