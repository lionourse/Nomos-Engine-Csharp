package fr.olympus.nomos.action;

import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.NumericMagicToken;
import fr.olympus.nomos.language.NumericOperatorToken;
import fr.olympus.nomos.language.TokenRegistry;
import fr.olympus.nomos.language.Token;
import fr.olympus.nomos.language.Tokenizer;
import fr.olympus.nomos.language.ValueTargetToken;
import fr.olympus.nomos.math.NumericExpressionParser;
import fr.olympus.nomos.math.NumericExpressionResult;
import fr.olympus.nomos.semantic.ResolvedToken;
import fr.olympus.nomos.semantic.SemanticAnalysisException;
import fr.olympus.nomos.semantic.SemanticContext;
import fr.olympus.nomos.semantic.SemanticPlan;
import fr.olympus.nomos.semantic.SemanticStrategy;
import fr.olympus.nomos.semantic.SequentialSemanticStrategy;
import fr.olympus.nomos.semantic.SpellVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Compiler from source text to a structured {@link SpellAction}.
 *
 * <p>A compiler instance supports concurrent calls when its supplied
 * {@link Tokenizer}, {@link SemanticStrategy}, and registered
 * {@link MagicToken} definitions are also stateless or thread-safe.</p>
 */
public class SpellActionCompiler {
    private final TokenRegistry registry;
    private final Tokenizer tokenizer;
    private final SemanticStrategy semanticStrategy;

    /**
     * Creates a compiler using the default sequential semantic strategy.
     *
     * @param registry registry used to resolve token names
     * @param tokenizer tokenizer used to split source text
     */
    public SpellActionCompiler(TokenRegistry registry, Tokenizer tokenizer) {
        this(registry, tokenizer, SequentialSemanticStrategy.INSTANCE);
    }

    /**
     * Creates a compiler using a custom semantic evaluation strategy.
     *
     * @param registry registry used to resolve token names
     * @param tokenizer tokenizer used to split source text
     * @param semanticStrategy strategy used to order resolved occurrences
     * @throws IllegalArgumentException when an argument is {@code null}
     */
    public SpellActionCompiler(TokenRegistry registry, Tokenizer tokenizer, SemanticStrategy semanticStrategy) {
        if (registry == null) {
            throw new IllegalArgumentException("TokenRegistry cannot be null.");
        }
        if (tokenizer == null) {
            throw new IllegalArgumentException("Tokenizer cannot be null.");
        }
        if (semanticStrategy == null) {
            throw new IllegalArgumentException("SemanticStrategy cannot be null.");
        }
        this.registry = registry;
        this.tokenizer = tokenizer;
        this.semanticStrategy = semanticStrategy;
    }

    /**
     * Compiles source text into a structured spell action.
     *
     * @param text source spell text; {@code null} is handled by the tokenizer
     * @return the compiled spell action
     * @throws SemanticAnalysisException when the semantic strategy fails or
     *         returns an invalid evaluation plan
     */
    public SpellAction compile(String text) {
        List<ResolvedToken> sourceTokens = resolveTokens(text);
        SemanticContext context = new SemanticContext(sourceTokens);
        SemanticPlan plan = analyze(context);
        validatePlan(context, plan);

        SpellAction action = new SpellAction(
                buildVector(sourceTokens),
                sourceTokens.stream().map(ResolvedToken::occurrence).toList()
        );
        applyContributions(plan.evaluationOrder().stream().map(ResolvedToken::definition).toList(), action);
        return action;
    }

    private List<ResolvedToken> resolveTokens(String text) {
        List<ResolvedToken> tokens = new ArrayList<>();
        List<String> rawTokens = tokenizer.tokenize(text);
        for (int sourceIndex = 0; sourceIndex < rawTokens.size(); sourceIndex++) {
            String rawToken = rawTokens.get(sourceIndex);
            MagicToken token = registry.get(rawToken);
            if (token != null) {
                tokens.add(new ResolvedToken(sourceIndex, token, new Token(rawToken, token.meaning())));
            }
        }
        return tokens;
    }

    private SemanticPlan analyze(SemanticContext context) {
        try {
            SemanticPlan plan = semanticStrategy.analyze(context);
            if (plan == null) {
                throw new SemanticAnalysisException("Semantic strategy returned a null plan.");
            }
            return plan;
        } catch (SemanticAnalysisException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new SemanticAnalysisException("Unexpected failure during semantic analysis.", exception);
        }
    }

    private void validatePlan(SemanticContext context, SemanticPlan plan) {
        List<ResolvedToken> sourceTokens = context.sourceTokens();
        List<ResolvedToken> evaluationOrder = plan.evaluationOrder();
        if (evaluationOrder.size() != sourceTokens.size()) {
            throw new SemanticAnalysisException(
                    "Semantic plan must contain every resolved source token exactly once."
            );
        }

        Map<Integer, ResolvedToken> sourceByIndex = new HashMap<>();
        for (ResolvedToken sourceToken : sourceTokens) {
            sourceByIndex.put(sourceToken.sourceIndex(), sourceToken);
        }

        Set<Integer> seenIndexes = new HashSet<>();
        for (ResolvedToken evaluatedToken : evaluationOrder) {
            ResolvedToken sourceToken = sourceByIndex.get(evaluatedToken.sourceIndex());
            if (sourceToken == null || !seenIndexes.add(evaluatedToken.sourceIndex())) {
                throw new SemanticAnalysisException(
                        "Semantic plan is not an exact permutation of the resolved source tokens."
                );
            }
            if (evaluatedToken.definition() != sourceToken.definition()
                    || evaluatedToken.occurrence() != sourceToken.occurrence()) {
                throw new SemanticAnalysisException(
                        "Semantic plan must reuse the original token definition and occurrence objects."
                );
            }
        }
    }

    private SpellVector buildVector(List<ResolvedToken> tokens) {
        SpellVector vector = new SpellVector();
        for (ResolvedToken resolved : tokens) {
            MagicToken token = resolved.definition();
            if (!isNumeric(token) && !(token instanceof NumericOperatorToken)) {
                vector.merge(resolved.occurrence().getMeaning());
            }
        }
        return vector;
    }

    private void applyContributions(List<MagicToken> tokens, SpellAction action) {
        NumericExpressionParser parser = new NumericExpressionParser();
        int index = 0;
        while (index < tokens.size()) {
            MagicToken token = tokens.get(index);
            if (isNumeric(token) || token instanceof NumericOperatorToken) {
                index++;
                continue;
            }

            if (acceptsValue(token) && index + 1 < tokens.size()) {
                ValueTargetToken valueTarget = (ValueTargetToken) token;
                NumericExpressionResult result = parser.parse(tokens, index + 1);
                if (result.isValid() && Double.isFinite(result.getValue())) {
                    applyContribution(action, valueTarget.actionWithValue(result.getValue()));
                    index += 1 + result.getConsumedTokens();
                    continue;
                }
            }

            applyContribution(action, token.action());
            index++;
        }
    }

    private boolean isNumeric(MagicToken token) {
        boolean numeric = token.isNumeric();
        if (numeric && !(token instanceof NumericMagicToken)) {
            throw new IllegalStateException("Token reports isNumeric() without extending NumericMagicToken: "
                    + token.getClass().getName());
        }
        return numeric;
    }

    private boolean acceptsValue(MagicToken token) {
        boolean acceptsValue = token.acceptsValue();
        if (acceptsValue && !(token instanceof ValueTargetToken)) {
            throw new IllegalStateException("Token reports acceptsValue() without extending ValueTargetToken: "
                    + token.getClass().getName());
        }
        return acceptsValue;
    }

    private void applyContribution(SpellAction action, ActionContribution contribution) {
        if (contribution == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : contribution.getProperties().entrySet()) {
            action.setProperty(entry.getKey(), entry.getValue());
        }
        contribution.getAddedNumbers().forEach(action::addNumericProperty);
        contribution.getSetNumbers().forEach(action::setNumericProperty);
        contribution.getAddedCosts().forEach(action.getCostProfile()::addCost);
        contribution.getCostMultipliers().forEach(action.getCostProfile()::multiplyCost);
        contribution.getConditions().forEach(action::addCondition);
    }
}
