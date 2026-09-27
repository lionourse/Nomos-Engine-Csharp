package fr.olympus.nomos.example.token;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.semantic.MagicMeaning;

@MagicTokenAnnotation(id = "shi", aliases = {"ice", "glace"})
public class IceToken extends MagicToken {
    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning()
                .add("element.ice", 1.0)
                .add("temperature.cold", 0.9)
                .add("control.slow", 0.8);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .set("element", "ice")
                .addNumber("power", 2.0)
                .addNumber("cold", 10.0)
                .addNumber("slow_strength", 3.0)
                .addCost("mana", 7.0);
    }
}
