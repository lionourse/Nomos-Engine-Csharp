package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.ValueTargetToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "duration", aliases = {"duree", "temps"})
public class DurationToken extends ValueTargetToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning().add("modifier.duration", 1.0);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .addNumber("duration", 5.0)
                .addCost("mana", 3.0);
    }

    @Override
    public ActionContribution actionWithValue(double value) {
        double duration = Math.max(0.0, value);
        return new ActionContribution()
                .setNumber("duration", duration)
                .addCost("mana", duration * 0.8);
    }
}
