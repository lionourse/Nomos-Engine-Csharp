package fr.olympus.nomos;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.action.SpellAction;
import fr.olympus.nomos.action.SpellActionCompiler;
import fr.olympus.nomos.action.SpellActionType;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.NumericMagicToken;
import fr.olympus.nomos.language.NumericOperatorToken;
import fr.olympus.nomos.language.NumericOperatorType;
import fr.olympus.nomos.language.TokenRegistry;
import fr.olympus.nomos.language.ValueTargetToken;
import fr.olympus.nomos.language.WhitespaceTokenizer;
import fr.olympus.nomos.semantic.MagicMeaning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpellPipelineTest {
    private TokenRegistry registry;
    private SpellActionCompiler compiler;

    @BeforeEach
    void setUp() {
        registry = new TokenRegistry();
        registry.register("add", new AddPowerToken());
        registry.register("set", new SetPowerToken());
        registry.register("two", new TwoToken());
        registry.register("three", new ThreeToken());
        registry.register("four", new FourToken());
        registry.register("plus", new PlusToken());
        registry.register("multiply", new MultiplyToken());
        registry.register("shape", new ProjectileToken());
        compiler = new SpellActionCompiler(registry, new WhitespaceTokenizer());
    }

    @Test
    void appliesAddAndSetInSourceOrder() {
        assertEquals(2.0, compiler.compile("add set two").getNumericProperty("power"));
        assertEquals(5.0, compiler.compile("set two add").getNumericProperty("power"));
    }

    @Test
    void ignoresUnknownWordsBeforeResolvingExpressions() {
        SpellAction action = compiler.compile("set ignored two");

        assertEquals(2.0, action.getNumericProperty("power"));
        assertEquals(List.of("set", "two"), action.getTokens().stream().map(token -> token.getValue()).toList());
    }

    @Test
    void evaluatesExpressionsWithPriorityAndLeftAssociativity() {
        SpellAction action = compiler.compile("set two plus three multiply four");

        assertEquals(14.0, action.getNumericProperty("power"));
    }

    @Test
    void incompleteExpressionFallsBackToDefaultContribution() {
        SpellAction action = compiler.compile("set two plus");

        assertEquals(1.0, action.getNumericProperty("power"));
        assertEquals(3, action.getTokens().size());
    }

    @Test
    void exposesSemanticTokenTraceAndTypedActionType() {
        SpellAction action = compiler.compile("SHAPE");

        assertEquals(SpellActionType.PROJECTILE, action.getActionType());
        assertEquals("projectile", action.getStringProperty("action_type", "unknown"));
        assertEquals(List.of("shape"), action.getTokens().stream().map(token -> token.getValue()).toList());
        assertEquals(1.0, action.getTokens().getFirst().getMeaning().getDimensions().get("shape.projectile"));
    }

    @Test
    void returnedTokenTraceIsImmutable() {
        SpellAction action = compiler.compile("add");

        assertThrows(UnsupportedOperationException.class, () -> action.getTokens().clear());
    }

    @Test
    void rejectsInconsistentCapabilityFlags() {
        registry.register("liar", new MagicToken() {
            @Override
            public boolean isNumeric() {
                return true;
            }
        });

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> compiler.compile("liar"));
        assertTrue(error.getMessage().contains("NumericMagicToken"));
    }

    @MagicTokenAnnotation(id = "add")
    static final class AddPowerToken extends MagicToken {
        @Override
        public ActionContribution action() {
            return new ActionContribution().addNumber("power", 3.0);
        }
    }

    @MagicTokenAnnotation(id = "set")
    static final class SetPowerToken extends ValueTargetToken {
        @Override
        public ActionContribution action() {
            return new ActionContribution().addNumber("power", 1.0);
        }

        @Override
        public ActionContribution actionWithValue(double value) {
            return new ActionContribution().setNumber("power", value);
        }
    }

    @MagicTokenAnnotation(id = "two")
    static final class TwoToken extends NumericMagicToken {
        @Override public double value() { return 2.0; }
    }

    @MagicTokenAnnotation(id = "three")
    static final class ThreeToken extends NumericMagicToken {
        @Override public double value() { return 3.0; }
    }

    @MagicTokenAnnotation(id = "four")
    static final class FourToken extends NumericMagicToken {
        @Override public double value() { return 4.0; }
    }

    @MagicTokenAnnotation(id = "plus")
    static final class PlusToken extends NumericOperatorToken {
        @Override public NumericOperatorType operatorType() { return NumericOperatorType.ADD; }
    }

    @MagicTokenAnnotation(id = "multiply")
    static final class MultiplyToken extends NumericOperatorToken {
        @Override public NumericOperatorType operatorType() { return NumericOperatorType.MULTIPLY; }
    }

    @MagicTokenAnnotation(id = "shape")
    static final class ProjectileToken extends MagicToken {
        @Override
        public MagicMeaning meaning() {
            return new MagicMeaning().add("shape.projectile", 1.0);
        }

        @Override
        public ActionContribution action() {
            return new ActionContribution().set("action_type", "projectile");
        }
    }
}
