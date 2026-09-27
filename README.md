# Nomos Engine

Nomos Engine is a framework-independent Java library for defining, discovering, compiling, validating, and casting token-based magic.

A spell is written as a sequence of registered words. Each word can contribute semantic dimensions, action properties, numeric values, resource costs, cost multipliers, or validation conditions. A configurable semantic strategy decides the evaluation order before Nomos compiles those contributions into a `SpellAction`. `SpellCastService` then validates the action and atomically consumes the caster's resources.

```text
ka ru far ten power five
```

The concrete words above are supplied by the `Exemple` module. The main library contains the engine and public extension contracts only.

## Requirements

- Java 25
- Gradle 9.2.0, available through the included wrapper

Nomos has one runtime dependency: ClassGraph `4.8.184`, used for annotation-based registration.

## Installation

Published coordinates are prepared as:

```groovy
dependencies {
    implementation 'fr.olympus-engine:Nomos-Engine:1.0.0'
}
```

Publication is performed manually by the project owner. The Gradle build generates a signed Central Portal bundle but never uploads it.

## Olympus lifecycle

Nomos follows the lifecycle used by the Olympus libraries:

```java
Nomos.init();
Nomos.autoRegister(RegisterType.TOKEN, "com.example.magic");

TokenRegistry registry = Nomos.getData().getTokenRegistry();
```

`Nomos.init()` must be called exactly once per JVM. Access before initialization or a second initialization attempt throws an `IllegalStateException`. Nomos intentionally has no reset or shutdown operation.

Scanning is always restricted to packages explicitly supplied by the caller. Annotated token classes need a no-argument constructor; public, protected, package-private, and private constructors are supported.

## Defining a token

```java
@MagicTokenAnnotation(id = "spark", aliases = {"lightning", "foudre"})
public final class SparkToken extends MagicToken {

    @Override
    public MagicMeaning meaning() {
        return new MagicMeaning()
                .add("element.lightning", 1.0)
                .add("damage.electric", 1.0);
    }

    @Override
    public ActionContribution action() {
        return new ActionContribution()
                .set("element", "lightning")
                .setActionType(SpellActionType.DIRECT_EFFECT)
                .addNumber("power", 4.0)
                .addCost("mana", 9.0);
    }
}
```

IDs and aliases are trimmed and normalized with `Locale.ROOT`. A definition is registered atomically: if its ID or one of its aliases collides with an existing name, none of its names are added and an `IllegalArgumentException` is thrown.

Manual registration is also available:

```java
Nomos.getData().registerToken(new SparkToken());
```

The registry keeps one token instance per definition and maps its ID and aliases to that same instance.

## Compiling and casting

```java
TokenRegistry registry = Nomos.getData().getTokenRegistry();
SpellActionCompiler compiler = new SpellActionCompiler(registry, new WhitespaceTokenizer());
SpellCastService castService = new SpellCastService(compiler);

SpellCaster caster = new SpellCaster("Merlin");
caster.getResources().set("mana", 100.0);
caster.getResources().set("focus", 20.0);

SpellAction preview = compiler.compile("spark power five");
SpellCost previewCost = preview.calculateCost();

SpellCastResult result = castService.cast(caster, "spark power five");
```

Compilation performs the following operations:

1. `Tokenizer` splits and normalizes the text.
2. `TokenRegistry` resolves known words; unknown words are ignored.
3. `SemanticStrategy` builds a validated evaluation plan.
4. `MagicMeaning` values are merged into a `SpellVector` in lexical order.
5. `ActionContribution` values are applied in semantic evaluation order.
6. Numeric expressions following a `ValueTargetToken` in that plan are evaluated.
7. The final resource cost is calculated.

The compiled action exposes its recognized lexical occurrences through `getTokens()`, its semantic vector, typed `SpellActionType`, generic and numeric properties, conditions, and cost profile.

## Custom semantic strategies

The two-argument compiler constructor preserves the original sequential behavior:

```java
var compiler = new SpellActionCompiler(registry, tokenizer);
```

Applications can inject another grammar without changing tokens, vectors, actions, or the numeric parser:

```java
SemanticStrategy strategy = context -> {
    List<ResolvedToken> evaluationOrder = arrangeForLanguage(context.sourceTokens());
    return new SemanticPlan(evaluationOrder);
};

var compiler = new SpellActionCompiler(registry, tokenizer, strategy);
```

The strategy receives an immutable `SemanticContext`. Every `ResolvedToken` contains its tokenizer position, registered singleton definition, and lexical occurrence. Its `SemanticPlan` must be an exact permutation of the recognized occurrences: it may reorder them, but cannot add, remove, duplicate, or replace one. Nomos rejects invalid plans with `SemanticAnalysisException` before applying any contribution.

This makes language-specific ordering possible. A grammar can, for example, transform a postfix value form into the internal `ValueTargetToken` followed by its numeric expression. The compiler and numeric precedence rules remain language-independent.

`SpellAction.getTokens()` and `SpellVector` retain lexical source order. Only contribution and expression evaluation follow the semantic plan. Unknown words are removed before semantic analysis; grammatical words needed by a strategy should therefore be registered as neutral `MagicToken` definitions.

Several compilers can share one `TokenRegistry` while using different strategies. Custom strategies must be stateless or thread-safe when a compiler is shared across threads.

## Numeric expressions

Numeric values and operators are extensions of `NumericMagicToken` and `NumericOperatorToken`. The engine supports these operator families:

| Priority | Operators |
|---:|---|
| 3 | power |
| 2 | multiply, divide, modulo |
| 1 | add, subtract |

Operators with the same priority are evaluated from left to right, including power. Parentheses, unary signs, and arbitrary numeric literals are not built in; an application defines its vocabulary through registered numeric tokens.

An incomplete or invalid expression preserves the historical MagicToken behavior and falls back to the target token's default contribution.

## Validation conditions

Conditions run before any resource is consumed. A condition can be attached globally to a `SpellCastService` or contributed by a token to one action.

```java
castService.addCondition(SpellCondition.contextual((currentCaster, action, validation) -> {
    if (action.getBooleanProperty("requires_target", false)) {
        validation.addError("A target is required.");
    }
}));
```

The original vector-only functional contract remains available:

```java
SpellCondition requiresFire = (vector, validation) -> {
    if (vector.get("element.fire") <= 0.0) {
        validation.addError("A fire component is required.");
    }
};
```

Validation failure returns a `SpellCastResult` containing the action, calculated cost, and `SpellValidationResult`. No resources are consumed.

## Thread safety

Nomos implementations of registries, semantic structures, actions, costs, validation results, cast conditions, and resource containers support concurrent access. Custom `MagicToken` and `Tokenizer` implementations must be stateless or thread-safe when their compiler is shared between threads. Complete spell payments use one atomic check-and-consume operation, so a failed payment never removes a subset of the required resources.

Snapshots returned by collection getters are immutable and remain stable if the source object later changes.

## Example module

The `Exemple` module contains the 29 concrete definitions from the original MagicToken project:

- 12 domain tokens;
- 11 numeric tokens from zero to ten;
- 6 numeric operators.

Run the complete demonstration with:

```powershell
.\gradlew.bat :Exemple:run
```

## Build and test

```powershell
.\gradlew.bat clean test build
```

The test suite covers registration, collisions, tokenization, expression evaluation, compilation order, costs, casting, conditions, numeric validation, and concurrent registration and payment.

## Central Portal bundle

Configure Gradle signing through your local user properties or environment, then run:

```powershell
.\gradlew.bat generateCentralBundle
```

The signed bundle is written to `build/libs/bundle.zip`. This task only creates the local bundle; it performs no network upload.

## License

No license has been selected yet.
