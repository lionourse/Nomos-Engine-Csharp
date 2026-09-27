package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.ValueTargetToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "power", aliases = {"puissance", "force"})
public class PowerToken extends ValueTargetToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning().add("modifier.power", 1.0);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .addNumber("power", 1.0)
                .addCost("mana", 2.0);
    }

    @Override
    public ActionContribution actionWithValue(double value) {
        double power = Math.max(0.0, value);
        return new ActionContribution()
                .setNumber("power", power)
                .addCost("mana", power * 2.0)
                .addCost("focus", power * 0.25);
    }
}
