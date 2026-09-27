package fr.olympus.nomos.language;

import fr.olympus.nomos.annotation.MagicTokenAnnotation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe registry mapping normalized identifiers and aliases to token instances.
 */
public class TokenRegistry {

    private final ConcurrentMap<String, MagicToken> tokens = new ConcurrentHashMap<>();
    private final Object registrationLock = new Object();

    /**
     * Registers one name for a token.
     *
     * @param name token identifier or alias
     * @param token token instance shared by every registered name
     * @throws IllegalArgumentException when an argument is invalid or the name is already registered
     */
    public void register(String name, MagicToken token) {
        String normalized = normalizeRequired(name, "Token name");
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null.");
        }

        synchronized (registrationLock) {
            MagicToken existing = tokens.putIfAbsent(normalized, token);
            if (existing != null) {
                throw new IllegalArgumentException("Token already registered: " + normalized);
            }
        }
    }

    /**
     * Atomically registers the annotated identifier and every alias of one token definition.
     * No name is added when any name in the definition collides.
     *
     * @param token token instance to register
     */
    public void register(MagicToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null.");
        }

        MagicTokenAnnotation definition = token.getClass().getAnnotation(MagicTokenAnnotation.class);
        if (definition == null) {
            throw new IllegalArgumentException(
                    "Token class must be annotated with @MagicTokenAnnotation: " + token.getClass().getName()
            );
        }

        List<String> names = definitionNames(definition);
        synchronized (registrationLock) {
            for (String name : names) {
                if (tokens.containsKey(name)) {
                    throw new IllegalArgumentException("Token already registered: " + name);
                }
            }
            names.forEach(name -> tokens.put(name, token));
        }
    }

    public MagicToken get(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return tokens.get(normalize(name));
    }

    public boolean contains(String name) {
        return get(name) != null;
    }

    /**
     * Returns an immutable point-in-time snapshot ordered by registered name.
     */
    public Map<String, MagicToken> getTokens() {
        synchronized (registrationLock) {
            return Collections.unmodifiableMap(new TreeMap<>(tokens));
        }
    }

    private static List<String> definitionNames(MagicTokenAnnotation definition) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        addDefinitionName(names, definition.id(), "Token id");
        for (String alias : definition.aliases()) {
            addDefinitionName(names, alias, "Token alias");
        }
        return List.copyOf(names);
    }

    private static void addDefinitionName(LinkedHashSet<String> names, String value, String label) {
        String normalized = normalizeRequired(value, label);
        if (!names.add(normalized)) {
            throw new IllegalArgumentException("Duplicate token identifier or alias: " + normalized);
        }
    }

    private static String normalizeRequired(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be null or blank.");
        }
        return normalize(value);
    }

    private static String normalize(String value) {
        return value.strip().toLowerCase(Locale.ROOT);
    }
}
