package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.ValueTargetToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "far", aliases = {"distance", "loin", "portee"})
public class DistanceToken extends ValueTargetToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning()
                .add("range.distance", 1.0)
                .add("movement.projectile", 0.4);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .addNumber("range", 15.0)
                .addNumber("speed", 2.0)
                .addCost("mana", 5.0)
                .addCost("focus", 1.0);
    }

    @Override
    public ActionContribution actionWithValue(double value) {
        double range = Math.max(0.0, value);
        return new ActionContribution()
                .setNumber("range", range)
                .addNumber("speed", 2.0)
                .addCost("mana", range * 0.6)
                .addCost("focus", range * 0.1);
    }
}
