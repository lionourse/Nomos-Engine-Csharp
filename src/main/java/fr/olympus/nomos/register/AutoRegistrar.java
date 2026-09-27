package fr.olympus.nomos.register;

import fr.olympus.nomos.Nomos;
import fr.olympus.nomos.annotation.MagicTokenAnnotation;
import fr.olympus.nomos.language.MagicToken;
import fr.olympus.nomos.resources.NomosData;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Constructor;

/**
 * Registers annotated Nomos components found on the classpath.
 */
public final class AutoRegistrar {

    private AutoRegistrar() {
    }

    /**
     * Registers components of the requested type from explicit packages.
     * Registrations completed before an error remain in the registry, matching
     * the registration behavior of the other Olympus libraries.
     *
     * @param type the component type to register
     * @param basePackages the packages to scan
     * @throws IllegalArgumentException if an argument is invalid
     * @throws IllegalStateException if an annotated class is invalid
     */
    public static void register(RegisterType type, String... basePackages) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        validatePackages(basePackages);

        NomosData data = Nomos.getData();

        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackages)
                .scan()) {
            if (type == RegisterType.ALL || type == RegisterType.TOKEN) {
                for (ClassInfo classInfo : scan.getClassesWithAnnotation(MagicTokenAnnotation.class.getName())) {
                    registerToken(data, classInfo.loadClass());
                }
            }
        }
    }

    private static void validatePackages(String[] basePackages) {
        if (basePackages == null || basePackages.length == 0) {
            throw new IllegalArgumentException("basePackages required.");
        }
        for (String basePackage : basePackages) {
            if (basePackage == null || basePackage.isBlank()) {
                throw new IllegalArgumentException("basePackages cannot contain null or blank values.");
            }
        }
    }

    private static void registerToken(NomosData data, Class<?> raw) {
        if (!MagicToken.class.isAssignableFrom(raw)) {
            throw new IllegalStateException("@MagicTokenAnnotation on non-MagicToken: " + raw.getName());
        }

        @SuppressWarnings("unchecked")
        Class<? extends MagicToken> tokenClass = (Class<? extends MagicToken>) raw;
        MagicToken token = newInstance(tokenClass);
        data.registerToken(token);
    }

    private static <T> T newInstance(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.trySetAccessible()) {
                throw new IllegalStateException("Cannot access no-arg constructor: " + type.getName());
            }
            return constructor.newInstance();
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("No-arg constructor required for auto-register: " + type.getName(), exception);
        } catch (ReflectiveOperationException | SecurityException exception) {
            throw new IllegalStateException("Cannot instantiate: " + type.getName(), exception);
        }
    }
}
