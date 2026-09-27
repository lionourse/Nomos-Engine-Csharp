package fr.olympus.nomos;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.cost.ResourceContainer;
import fr.olympus.nomos.cost.SpellCost;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.TokenRegistry;
import fr.olympus.nomos.language.WhitespaceTokenizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistryAndConcurrencyTest {

    @Test
    void registersAnnotatedIdAndAliasesWithOneSharedInstance() {
        TokenRegistry registry = new TokenRegistry();
        FireToken token = new FireToken();

        registry.register(token);

        assertSame(token, registry.get("fire"));
        assertSame(token, registry.get(" FLAME "));
        assertSame(token, registry.get("BRAISE"));
        assertEquals(3, registry.getTokens().size());
    }

    @Test
    void rejectsUnannotatedTokens() {
        TokenRegistry registry = new TokenRegistry();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registry.register(new UnannotatedToken())
        );

        assertTrue(exception.getMessage().contains("@MagicTokenAnnotation"));
        assertTrue(registry.getTokens().isEmpty());
    }

    @Test
    void annotationRegistrationIsAtomicWhenAnAliasCollides() {
        TokenRegistry registry = new TokenRegistry();
        MagicToken existing = new UnannotatedToken();
        registry.register("occupied", existing);

        assertThrows(IllegalArgumentException.class, () -> registry.register(new CollidingToken()));

        assertSame(existing, registry.get("occupied"));
        assertFalse(registry.contains("candidate"));
        assertFalse(registry.contains("unused"));
        assertEquals(1, registry.getTokens().size());
    }

    @Test
    @ResourceLock(Resources.LOCALE)
    void tokenizerUsesRootLocaleInsteadOfJvmDefaultLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertEquals(List.of("identifier", "incantation"),
                    new WhitespaceTokenizer().tokenize("IDENTIFIER\tINCANTATION"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void registrySupportsConcurrentRegistrationsAndReads() throws Exception {
        TokenRegistry registry = new TokenRegistry();
        MagicToken token = new UnannotatedToken();
        int registrations = 256;
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(12)) {
            List<Future<?>> tasks = new ArrayList<>();
            for (int index = 0; index < registrations; index++) {
                int id = index;
                tasks.add(executor.submit(() -> {
                    await(start);
                    registry.register("token-" + id, token);
                    assertSame(token, registry.get("TOKEN-" + id));
                }));
            }
            for (int reader = 0; reader < 4; reader++) {
                tasks.add(executor.submit(() -> {
                    await(start);
                    for (int iteration = 0; iteration < 500; iteration++) {
                        registry.getTokens().forEach((name, registered) -> {
                            assertTrue(name.startsWith("token-"));
                            assertSame(token, registered);
                        });
                    }
                }));
            }

            start.countDown();
            for (Future<?> task : tasks) {
                task.get(10, TimeUnit.SECONDS);
            }
        }

        assertEquals(registrations, registry.getTokens().size());
        for (int index = 0; index < registrations; index++) {
            assertSame(token, registry.get("token-" + index));
        }
    }

    @Test
    void resourcePaymentDoesNotDoubleSpendUnderConcurrency() throws Exception {
        ResourceContainer resources = new ResourceContainer();
        resources.set("mana", 100.0);
        SpellCost payment = new SpellCost();
        payment.add("mana", 1.0);
        int attempts = 400;
        AtomicInteger successes = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(16)) {
            List<Future<?>> tasks = new ArrayList<>();
            for (int attempt = 0; attempt < attempts; attempt++) {
                tasks.add(executor.submit(() -> {
                    await(start);
                    if (resources.tryConsume(payment)) {
                        successes.incrementAndGet();
                    }
                    assertTrue(resources.get("mana") >= 0.0);
                }));
            }

            start.countDown();
            for (Future<?> task : tasks) {
                task.get(10, TimeUnit.SECONDS);
            }
        }

        assertEquals(100, successes.get());
        assertEquals(0.0, resources.get("mana"));
        assertTrue(resources.getResources().values().stream().allMatch(amount -> amount >= 0.0));
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for concurrent test start", exception);
        }
    }

    @MagicTokenAnnotation(id = "fire", aliases = {"flame", "braise"})
    private static final class FireToken extends MagicToken {
    }

    @MagicTokenAnnotation(id = "candidate", aliases = {"occupied", "unused"})
    private static final class CollidingToken extends MagicToken {
    }

    private static final class UnannotatedToken extends MagicToken {
    }
}
