package fr.olympus.nomos;

import fr.olympus.nomos.action.ActionContribution;
import fr.olympus.nomos.action.SpellAction;
import fr.olympus.nomos.action.SpellActionCompiler;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.NumericMagicToken;
import fr.olympus.nomos.language.NumericOperatorToken;
import fr.olympus.nomos.language.NumericOperatorType;
import fr.olympus.nomos.language.Token;
import fr.olympus.nomos.language.TokenRegistry;
import fr.olympus.nomos.language.ValueTargetToken;
import fr.olympus.nomos.language.WhitespaceTokenizer;
import fr.olympus.nomos.semantic.MagicMeaning;
import fr.olympus.nomos.semantic.ResolvedToken;
import fr.olympus.nomos.semantic.SemanticAnalysisException;
import fr.olympus.nomos.semantic.SemanticContext;
import fr.olympus.nomos.semantic.SemanticPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemanticStrategyValidationAndConcurrencyTest {
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
    }

    @Test
    void compilerRejectsNullStrategy() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SpellActionCompiler(registry, new WhitespaceTokenizer(), null)
        );
    }

    @Test
    void compilerRejectsNullPlanReturnedByStrategy() {
        SpellActionCompiler compiler = compilerWith(context -> null);

        SemanticAnalysisException error = assertThrows(
                SemanticAnalysisException.class,
                () -> compiler.compile("add")
        );

        assertTrue(error.getMessage().contains("null plan"));
    }

    @Test
    void semanticContractsRejectNullListsAndElements() {
        ResolvedToken token = resolvedToken(0, "add", new AddPowerToken());

        assertThrows(NullPointerException.class, () -> new SemanticContext(null));
        assertThrows(NullPointerException.class, () -> new SemanticContext(java.util.Arrays.asList(token, null)));
        assertThrows(NullPointerException.class, () -> new SemanticPlan(null));
        assertThrows(NullPointerException.class, () -> new SemanticPlan(java.util.Arrays.asList(token, null)));
    }

    @Test
    void compilerRejectsInjectedOccurrence() {
        SpellActionCompiler compiler = compilerWith(context -> new SemanticPlan(List.of(
                context.sourceTokens().getFirst(),
                resolvedToken(99, "foreign", new AddPowerToken())
        )));

        assertThrows(SemanticAnalysisException.class, () -> compiler.compile("add set"));
    }

    @Test
    void compilerRejectsMissingOccurrence() {
        SpellActionCompiler compiler = compilerWith(context -> new SemanticPlan(List.of(
                context.sourceTokens().getFirst()
        )));

        assertThrows(SemanticAnalysisException.class, () -> compiler.compile("add set"));
    }

    @Test
    void compilerRejectsDuplicatedOccurrence() {
        SpellActionCompiler compiler = compilerWith(context -> new SemanticPlan(List.of(
                context.sourceTokens().getFirst(),
                context.sourceTokens().getFirst()
        )));

        assertThrows(SemanticAnalysisException.class, () -> compiler.compile("add add"));
    }

    @Test
    void compilerRejectsRecreatedOccurrenceWithMatchingSourceIndex() {
        SpellActionCompiler compiler = compilerWith(context -> {
            ResolvedToken original = context.sourceTokens().getFirst();
            ResolvedToken recreated = new ResolvedToken(
                    original.sourceIndex(),
                    original.definition(),
                    new Token(original.occurrence().getValue(), original.occurrence().getMeaning())
            );
            return new SemanticPlan(List.of(recreated));
        });

        assertThrows(SemanticAnalysisException.class, () -> compiler.compile("add"));
    }

    @Test
    void semanticContextAndPlanAreImmutableDefensiveCopies() {
        ResolvedToken token = resolvedToken(0, "add", new AddPowerToken());
        List<ResolvedToken> contextSource = new ArrayList<>(List.of(token));
        SemanticContext context = new SemanticContext(contextSource);
        contextSource.clear();

        assertEquals(List.of(token), context.sourceTokens());
        assertThrows(UnsupportedOperationException.class, () -> context.sourceTokens().clear());

        List<ResolvedToken> planSource = new ArrayList<>(List.of(token));
        SemanticPlan plan = new SemanticPlan(planSource);
        planSource.clear();

        assertEquals(List.of(token), plan.evaluationOrder());
        assertThrows(UnsupportedOperationException.class, () -> plan.evaluationOrder().clear());
    }

    @Test
    void compilerSuppliesAnImmutableSemanticContext() {
        AtomicBoolean immutable = new AtomicBoolean();
        SpellActionCompiler compiler = compilerWith(context -> {
            assertThrows(UnsupportedOperationException.class, () -> context.sourceTokens().clear());
            immutable.set(true);
            return SemanticPlan.sequential(context);
        });

        compiler.compile("add");

        assertTrue(immutable.get());
    }

    @Test
    void unexpectedStrategyFailureIsWrapped() {
        SpellActionCompiler compiler = compilerWith(context -> {
            throw new IllegalStateException("broken strategy");
        });

        SemanticAnalysisException error = assertThrows(
                SemanticAnalysisException.class,
                () -> compiler.compile("add")
        );

        assertInstanceOf(IllegalStateException.class, error.getCause());
    }

    @Test
    void sharedCompilerWithStatelessStrategySupportsConcurrentCompilation() throws Exception {
        SpellActionCompiler compiler = compilerWith(context -> new SemanticPlan(List.of(
                context.sourceTokens().get(5),
                context.sourceTokens().get(0),
                context.sourceTokens().get(1),
                context.sourceTokens().get(2),
                context.sourceTokens().get(3),
                context.sourceTokens().get(4),
                context.sourceTokens().get(6)
        )));
        int compilations = 300;
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(16)) {
            List<Future<?>> tasks = new ArrayList<>();
            for (int index = 0; index < compilations; index++) {
                tasks.add(executor.submit(() -> {
                    await(start);
                    SpellAction action = compiler.compile("two plus three multiply four set add");
                    assertEquals(17.0, action.getNumericProperty("power"));
                    assertEquals(
                            List.of("two", "plus", "three", "multiply", "four", "set", "add"),
                            action.getTokens().stream().map(Token::getValue).toList()
                    );
                }));
            }

            start.countDown();
            for (Future<?> task : tasks) {
                task.get(10, TimeUnit.SECONDS);
            }
        }
    }

    private SpellActionCompiler compilerWith(fr.olympus.nomos.semantic.SemanticStrategy strategy) {
        return new SpellActionCompiler(registry, new WhitespaceTokenizer(), strategy);
    }

    private static ResolvedToken resolvedToken(int index, String value, MagicToken definition) {
        return new ResolvedToken(index, definition, new Token(value, definition.meaning()));
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while awaiting concurrent compilation", exception);
        }
    }

    static final class AddPowerToken extends MagicToken {
        @Override
        public ActionContribution action() {
            return new ActionContribution().addNumber("power", 3.0);
        }
    }

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
}
