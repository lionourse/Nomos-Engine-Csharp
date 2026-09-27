package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "stable", aliases = {"stabilise", "stabiliser"})
public class StableToken extends MagicToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning().add("modifier.stability", 1.0);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .addNumber("stability", 1.0)
                .multiplyCost("mana", 1.25);
    }
}
