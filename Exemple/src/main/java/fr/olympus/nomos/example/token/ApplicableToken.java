package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "apply", aliases = {"applicable", "appliquer"})
public class ApplicableToken extends MagicToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning().add("behavior.applicable", 1.0);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution().set("applicable", true);
    }
}
