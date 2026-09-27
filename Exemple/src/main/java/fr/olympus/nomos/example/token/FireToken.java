package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "ka", aliases = {"fire", "feu"})
public class FireToken extends MagicToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning()
                .add("element.fire", 1.0)
                .add("temperature.hot", 0.8)
                .add("damage.burn", 1.0);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .set("element", "fire")
                .addNumber("power", 3.0)
                .addNumber("heat", 10.0)
                .addNumber("burn_damage", 4.0)
                .addCost("mana", 8.0);
    }
}
