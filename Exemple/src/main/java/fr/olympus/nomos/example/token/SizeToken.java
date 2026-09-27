package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.ValueTargetToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "size", aliases = {"taille", "grand"})
public class SizeToken extends ValueTargetToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning().add("modifier.size", 1.0);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .addNumber("size", 1.0)
                .addCost("mana", 2.0);
    }

    @Override
    public ActionContribution actionWithValue(double value) {
        double size = Math.max(0.0, value);
        return new ActionContribution()
                .setNumber("size", size)
                .addCost("mana", size * 1.5);
    }
}
