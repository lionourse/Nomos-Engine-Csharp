package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "ru", aliases = {"sphere", "boule"})
public class SphereToken extends MagicToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning()
                .add("shape.sphere", 1.0)
                .add("movement.projectile", 0.7);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .set("shape", "sphere")
                .set("action_type", "projectile")
                .addNumber("speed", 4.0)
                .addNumber("radius", 1.0)
                .addCost("mana", 4.0);
    }
}
