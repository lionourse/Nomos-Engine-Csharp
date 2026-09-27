package fr.olympus.nomos.language;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.semantic.MagicMeaning;

/**
 * Base type for every token understood by the Nomos compiler.
 *
 * <p>One definition instance is shared by every compiled occurrence. Custom
 * implementations must therefore be stateless or thread-safe.</p>
 */
public abstract class MagicToken {

    public MagicMeaning meaning() {
        return new MagicMeaning();
    }

    public ActionContribution action() {
        return new ActionContribution();
    }

    public boolean isNumeric() {
        return false;
    }

    public boolean acceptsValue() {
        return false;
    }
}
