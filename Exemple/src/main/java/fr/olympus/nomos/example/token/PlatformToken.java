package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "tal", aliases = {"platform", "plateforme"})
public class PlatformToken extends MagicToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning()
                .add("shape.platform", 1.0)
                .add("construct.solid", 1.0)
                .add("movement.static", 1.0);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .set("shape", "platform")
                .set("action_type", "construct")
                .addNumber("width", 3.0)
                .addNumber("height", 0.4)
                .addNumber("duration", 10.0)
                .addCost("mana", 10.0)
                .addCost("focus", 2.0);
    }
}
