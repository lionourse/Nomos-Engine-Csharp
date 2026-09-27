package fr.olympus.nomos;

import fr.olympus.nomos.register.AutoRegistrar;
import fr.olympus.nomos.register.RegisterType;
import fr.olympus.nomos.resources.NomosData;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Main entry point for the Nomos library.
 */
public final class Nomos {

    private static final AtomicReference<Nomos> INSTANCE = new AtomicReference<>();

    private final NomosData data;

    private Nomos() {
        this.data = new NomosData();
    }

    /**
     * Initializes Nomos.
     *
     * @return the initialized Nomos instance
     * @throws IllegalStateException if Nomos is already initialized
     */
    public static Nomos init() {
        Nomos created = new Nomos();
        if (!INSTANCE.compareAndSet(null, created)) {
            throw new IllegalStateException("Nomos is already initialized.");
        }
        return created;
    }

    /**
     * Scans the supplied packages and registers matching Nomos components.
     *
     * @param type the component type to register
     * @param basePackages the packages to scan
     */
    public static void autoRegister(RegisterType type, String... basePackages) {
        AutoRegistrar.register(type, basePackages);
    }

    private static Nomos getInstance() {
        Nomos instance = INSTANCE.get();
        if (instance == null) {
            throw new IllegalStateException("Nomos is not initialized yet.");
        }
        return instance;
    }

    /**
     * Returns the central Nomos registry container.
     *
     * @return the Nomos data container
     */
    public static NomosData getData() {
        return getInstance().data;
    }
}
