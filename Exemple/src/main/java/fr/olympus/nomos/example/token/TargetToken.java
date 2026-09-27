package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "target", aliases = {"cible"})
public class TargetToken extends MagicToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning()
                .add("target.required", 1.0)
                .add("target.entity", 0.8);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .set("requires_target", true)
                .set("target_type", "entity");
    }
}
