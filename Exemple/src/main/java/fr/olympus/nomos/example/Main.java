package fr.olympus.nomos.example;

import fr.olympus.nomos.Nomos;
import fr.olympus.nomos.action.SpellAction;
import fr.olympus.nomos.action.SpellActionCompiler;
import fr.olympus.nomos.cast.SpellCastResult;
import fr.olympus.nomos.cast.SpellCastService;
import fr.olympus.nomos.cast.SpellCaster;
import fr.olympus.nomos.language.TokenRegistry;
import fr.olympus.nomos.language.WhitespaceTokenizer;
import fr.olympus.nomos.register.RegisterType;
import fr.olympus.nomos.resources.NomosData;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Nomos.init();
        Nomos.autoRegister(RegisterType.TOKEN, "fr.olympus.nomos.example.token");

        NomosData data = Nomos.getData();
        TokenRegistry registry = data.getTokenRegistry();
        SpellActionCompiler compiler = new SpellActionCompiler(registry, new WhitespaceTokenizer());

        String incantation = "feu boule puissance deux plus trois loin dix stable";
        SpellAction preview = compiler.compile(incantation);

        System.out.println("Incantation: " + incantation);
        System.out.println("Registered names: " + registry.getTokens().size());
        System.out.println("Recognized tokens: " + preview.getTokens().size());
        System.out.println("Semantic vector: " + preview.getVector().getValues());
        System.out.println("Action properties: " + preview.getProperties());
        System.out.println("Numeric properties: " + preview.getNumericProperties());
        System.out.println("Preview cost: " + preview.calculateCost());

        SpellCaster caster = new SpellCaster("Olympus mage");
        caster.getResources().set("mana", 100.0);
        caster.getResources().set("focus", 20.0);

        SpellCastService castService = new SpellCastService(compiler);
        SpellCastResult result = castService.cast(caster, incantation);

        System.out.println("Cast success: " + result.isSuccess());
        System.out.println("Cast message: " + result.getMessage());
        System.out.println("Paid cost: " + result.getCost());
        System.out.println("Remaining resources: " + caster.getResources().getResources());
    }
}
