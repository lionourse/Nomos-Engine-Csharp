package fr.olympus.nomos;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.action.SpellAction;
import fr.olympus.nomos.action.SpellActionCompiler;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.NumericMagicToken;
import fr.olympus.nomos.language.NumericOperatorToken;
import fr.olympus.nomos.language.NumericOperatorType;
import fr.olympus.nomos.language.TokenRegistry;
import fr.olympus.nomos.language.ValueTargetToken;
import fr.olympus.nomos.language.WhitespaceTokenizer;
import fr.olympus.nomos.semantic.MagicMeaning;
import fr.olympus.nomos.semantic.SemanticContext;
import fr.olympus.nomos.semantic.SemanticPlan;
import fr.olympus.nomos.semantic.SemanticStrategy;
import fr.olympus.nomos.semantic.SequentialSemanticStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SemanticStrategyTest {
    private TokenRegistry registry;

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
        registry.register(new MarkerToken());
    }

    @Test
    void historicalConstructorPreservesSequentialSourceOrder() {
        SpellActionCompiler compiler = new SpellActionCompiler(registry, new WhitespaceTokenizer());

        assertEquals(2.0, compiler.compile("add set two").getNumericProperty("power"));
        assertEquals(5.0, compiler.compile("set two add").getNumericProperty("power"));
    }

    @Test
    void explicitSequentialStrategyMatchesHistoricalConstructor() {
        SpellActionCompiler historical = new SpellActionCompiler(registry, new WhitespaceTokenizer());
        SpellActionCompiler explicit = new SpellActionCompiler(
                registry,
                new WhitespaceTokenizer(),
                SequentialSemanticStrategy.INSTANCE
        );

        SpellAction expected = historical.compile("mark set two plus three add");
        SpellAction actual = explicit.compile("mark set two plus three add");

        assertEquals(expected.getProperties(), actual.getProperties());
        assertEquals(expected.getNumericProperties(), actual.getNumericProperties());
        assertEquals(expected.getVector().getValues(), actual.getVector().getValues());
        assertEquals(tokenValues(expected), tokenValues(actual));
    }

    @Test
    void alternativeStrategyChangesContributionOrder() {
        SpellActionCompiler compiler = compilerWith(context -> order(context, 2, 0, 1));

        SpellAction action = compiler.compile("set two add");

        assertEquals(2.0, action.getNumericProperty("power"));
    }

    @Test
    void alternativeStrategySupportsPostfixedValueTarget() {
        SpellActionCompiler compiler = compilerWith(context -> order(context, 5, 0, 1, 2, 3, 4));

        SpellAction action = compiler.compile("two plus three multiply four set");

        assertEquals(14.0, action.getNumericProperty("power"));
        assertEquals(
                List.of("two", "plus", "three", "multiply", "four", "set"),
                tokenValues(action)
        );
    }

    @Test
    void semanticOrderingDoesNotChangeLexicalTraceOrVector() {
        SpellActionCompiler compiler = compilerWith(context -> order(context, 4, 2, 3, 0, 1));

        SpellAction action = compiler.compile("mark glyph set two add");

        assertEquals(List.of("mark", "glyph", "set", "two", "add"), tokenValues(action));
        assertEquals(2.0, action.getVector().get("semantic.marker"));
        assertEquals(1.0, action.getVector().get("semantic.set"));
        assertEquals(1.0, action.getVector().get("semantic.add"));
        assertEquals(0.0, action.getVector().get("numeric.two"));
    }

    @Test
    void reorderedNumericExpressionKeepsOperatorPriority() {
        SpellActionCompiler compiler = compilerWith(context -> order(context, 5, 0, 1, 2, 3, 4));

        SpellAction action = compiler.compile("two plus three multiply four set");

        assertEquals(14.0, action.getNumericProperty("power"));
    }

    @Test
    void incompleteReorderedExpressionUsesDefaultContribution() {
        SpellActionCompiler compiler = compilerWith(context -> order(context, 2, 0, 1));

        SpellAction action = compiler.compile("two plus set");

        assertEquals(1.0, action.getNumericProperty("power"));
        assertEquals(List.of("two", "plus", "set"), tokenValues(action));
    }

    @Test
    void unknownWordsAreRemovedBeforeSemanticAnalysis() {
        AtomicReference<List<String>> strategyInput = new AtomicReference<>();
        AtomicReference<List<Integer>> sourceIndexes = new AtomicReference<>();
        SpellActionCompiler compiler = compilerWith(context -> {
            strategyInput.set(context.sourceTokens().stream()
                    .map(token -> token.occurrence().getValue())
                    .toList());
            sourceIndexes.set(context.sourceTokens().stream()
                    .map(token -> token.sourceIndex())
                    .toList());
            return order(context, 1, 0);
        });

        SpellAction action = compiler.compile("two ignored set");

        assertEquals(List.of("two", "set"), strategyInput.get());
        assertEquals(List.of(0, 2), sourceIndexes.get());
        assertEquals(List.of("two", "set"), tokenValues(action));
        assertEquals(2.0, action.getNumericProperty("power"));
    }

    private SpellActionCompiler compilerWith(SemanticStrategy strategy) {
        return new SpellActionCompiler(registry, new WhitespaceTokenizer(), strategy);
    }

    private static SemanticPlan order(SemanticContext context, int... positions) {
        return new SemanticPlan(java.util.Arrays.stream(positions)
                .mapToObj(context.sourceTokens()::get)
                .toList());
    }

    private static List<String> tokenValues(SpellAction action) {
        return action.getTokens().stream().map(token -> token.getValue()).toList();
    }

    static final class AddPowerToken extends MagicToken {
        @Override
        public MagicMeaning meaning() {
            return new MagicMeaning().add("semantic.add", 1.0);
        }

        @Override
        public ActionContribution action() {
            return new ActionContribution().addNumber("power", 3.0);
        }
    }

    static final class SetPowerToken extends ValueTargetToken {
        @Override
        public MagicMeaning meaning() {
            return new MagicMeaning().add("semantic.set", 1.0);
        }

        @Override
        public ActionContribution action() {
            return new ActionContribution().addNumber("power", 1.0);
        }

        @Override
        public ActionContribution actionWithValue(double value) {
            return new ActionContribution().setNumber("power", value);
        }
    }

    static final class TwoToken extends NumericMagicToken {
        @Override public double value() { return 2.0; }
    }

    static final class ThreeToken extends NumericMagicToken {
        @Override public double value() { return 3.0; }
    }

    static final class FourToken extends NumericMagicToken {
        @Override public double value() { return 4.0; }
    }

    static final class PlusToken extends NumericOperatorToken {
        @Override public NumericOperatorType operatorType() { return NumericOperatorType.ADD; }
    }

    static final class MultiplyToken extends NumericOperatorToken {
        @Override public NumericOperatorType operatorType() { return NumericOperatorType.MULTIPLY; }
    }

    @MagicTokenAnnotation(id = "mark", aliases = "glyph")
    static final class MarkerToken extends MagicToken {
        @Override
        public MagicMeaning meaning() {
            return new MagicMeaning().add("semantic.marker", 1.0);
        }
    }
}
