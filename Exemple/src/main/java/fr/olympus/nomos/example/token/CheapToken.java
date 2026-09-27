package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "cheap", aliases = {"economique", "leger"})
public class CheapToken extends MagicToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning()
                .add("modifier.cost_reduction", 1.0)
                .add("modifier.instability", 0.5);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .addNumber("instability", 0.5)
                .multiplyCost("mana", 0.75);
    }
}
