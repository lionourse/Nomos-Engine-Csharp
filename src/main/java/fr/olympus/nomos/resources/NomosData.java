package fr.olympus.nomos.resources;

import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.language.TokenRegistry;

/**
 * Central registry container for Nomos.
 */
public final class NomosData {

    private final TokenRegistry tokenRegistry = new TokenRegistry();

    /**
     * Creates an empty Nomos registry container.
     */
    public NomosData() {
    }

    /**
     * Returns the shared token registry.
     *
     * @return the token registry
     */
    public TokenRegistry getTokenRegistry() {
        return tokenRegistry;
    }

    /**
     * Registers a token under an identifier.
     *
     * @param id the token identifier or alias
     * @param token the single token definition instance
     */
    public void registerToken(String id, MagicToken token) {
        tokenRegistry.register(id, token);
    }

    /**
     * Registers a token from its {@code MagicTokenAnnotation} metadata.
     *
     * @param token the single token definition instance
     */
    public void registerToken(MagicToken token) {
        tokenRegistry.register(token);
    }
}
