package fr.olympus.nomos;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.action.SpellActionCompiler;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.cast.SpellCastResult;
import fr.olympus.nomos.cast.SpellCastService;
import fr.olympus.nomos.cast.SpellCaster;
import fr.olympus.nomos.condition.SpellCondition;
import fr.olympus.nomos.cost.CostProfile;
import fr.olympus.nomos.cost.SpellCost;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.TokenRegistry;
import fr.olympus.nomos.language.WhitespaceTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CostAndCastTest {
    private SpellActionCompiler compiler;

    @BeforeEach
    void setUp() {
        TokenRegistry registry = new TokenRegistry();
        registry.register("priced", new PricedToken());
        registry.register("guarded", new GuardedToken());
        compiler = new SpellActionCompiler(registry, new WhitespaceTokenizer());
    }

    @Test
    void calculatesAddedCostsAndMultipliersIndependently() {
        CostProfile profile = new CostProfile();
        profile.addCost("MANA", 8.0);
        profile.addCost("mana", 4.0);
        profile.multiplyCost("mana", 0.5);
        profile.multiplyCost("mana", 1.5);

        SpellCost cost = profile.calculateFinalCost();

        assertEquals(9.0, cost.get("mana"));
        assertEquals(12.0, profile.getAddedCost("mana"));
        assertEquals(0.75, profile.getMultiplier("mana"));
    }

    @Test
    void successfulCastConsumesEveryCostOnce() {
        SpellCaster caster = casterWith(10.0, 5.0);

        SpellCastResult result = new SpellCastService(compiler).cast(caster, "priced");

        assertTrue(result.isSuccess());
        assertEquals(2.0, caster.getResources().get("mana"));
        assertEquals(3.0, caster.getResources().get("focus"));
    }

    @Test
    void failedCastDoesNotPartiallyConsumeResources() {
        SpellCaster caster = casterWith(100.0, 1.0);

        SpellCastResult result = new SpellCastService(compiler).cast(caster, "priced");

        assertFalse(result.isSuccess());
        assertEquals(100.0, caster.getResources().get("mana"));
        assertEquals(1.0, caster.getResources().get("focus"));
        assertTrue(result.getMessage().contains("focus"));
    }

    @Test
    void globalConditionRejectsBeforePaymentWithCasterContext() {
        SpellCaster caster = casterWith(10.0, 5.0);
        SpellCondition condition = SpellCondition.contextual((actualCaster, action, validation) -> {
            if (!"allowed".equals(actualCaster.getName())) {
                validation.addError("caster denied");
            }
        });
        SpellCastService service = new SpellCastService(compiler);
        service.addCondition(condition);

        SpellCastResult result = service.cast(caster, "priced");

        assertFalse(result.isSuccess());
        assertEquals(10.0, caster.getResources().get("mana"));
        assertEquals("caster denied", result.getValidationResult().getErrors().getFirst());
    }

    @Test
    void actionConditionContributedByTokenRejectsBeforePayment() {
        SpellCaster caster = casterWith(10.0, 5.0);

        SpellCastResult result = new SpellCastService(compiler).cast(caster, "priced guarded");

        assertFalse(result.isSuccess());
        assertEquals(10.0, caster.getResources().get("mana"));
        assertEquals("action denied", result.getValidationResult().getErrors().getFirst());
    }

    private static SpellCaster casterWith(double mana, double focus) {
        SpellCaster caster = new SpellCaster("denied");
        caster.getResources().set("mana", mana);
        caster.getResources().set("focus", focus);
        return caster;
    }

    @MagicTokenAnnotation(id = "priced")
    static final class PricedToken extends MagicToken {
        @Override
        public ActionContribution action() {
            return new ActionContribution().addCost("mana", 8.0).addCost("focus", 2.0);
        }
    }

    @MagicTokenAnnotation(id = "guarded")
    static final class GuardedToken extends MagicToken {
        @Override
        public ActionContribution action() {
            return new ActionContribution().addCondition((vector, validation) -> validation.addError("action denied"));
        }
    }
}
