package fr.olympus.nomos;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.action.SpellAction;
import fr.olympus.nomos.cost.CostProfile;
import fr.olympus.nomos.cost.ResourceContainer;
import fr.olympus.nomos.cost.SpellCost;
import fr.olympus.nomos.math.NumericExpression;
import fr.olympus.nomos.semantic.MagicMeaning;
import fr.olympus.nomos.semantic.SpellVector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumericValidationTest {
    @Test
    void buildersRejectNonFiniteNumbersWithoutStateCorruption() {
        ActionContribution contribution = new ActionContribution().addNumber("power", 2.0);

        assertThrows(IllegalArgumentException.class, () -> contribution.addNumber("power", Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> contribution.addCost("mana", Double.POSITIVE_INFINITY));
        assertEquals(2.0, contribution.getAddedNumbers().get("power"));
        assertFalse(contribution.getAddedCosts().containsKey("mana"));
    }

    @Test
    void mutableRuntimeContainersIgnoreNonFiniteValues() {
        SpellCost cost = new SpellCost();
        cost.add("mana", Double.NaN);
        CostProfile profile = new CostProfile();
        profile.addCost("mana", Double.POSITIVE_INFINITY);
        ResourceContainer resources = new ResourceContainer();
        resources.set("mana", Double.NEGATIVE_INFINITY);
        SpellAction action = new SpellAction(new SpellVector());
        action.setNumericProperty("power", Double.NaN);

        assertEquals(0.0, cost.get("mana"));
        assertEquals(0.0, profile.getAddedCost("mana"));
        assertEquals(0.0, resources.get("mana"));
        assertEquals(0.0, action.getNumericProperty("power"));
    }

    @Test
    void semanticAndExpressionApisRejectNonFiniteValues() {
        MagicMeaning meaning = new MagicMeaning();
        SpellVector vector = new SpellVector();
        NumericExpression expression = new NumericExpression();

        assertThrows(IllegalArgumentException.class, () -> meaning.add("power", Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> vector.add("power", Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> expression.addValue(Double.NEGATIVE_INFINITY));
    }
}
