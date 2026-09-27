package fr.olympus.nomos.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the identifiers used to register a magic token.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MagicTokenAnnotation {

    /**
     * Returns the primary token identifier.
     *
     * @return the primary identifier
     */
    String id();

    /**
     * Returns the optional alternative identifiers.
     *
     * @return the token aliases
     */
    String[] aliases() default {};
}
