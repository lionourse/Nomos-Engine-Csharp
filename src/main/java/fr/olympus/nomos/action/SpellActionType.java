package fr.olympus.nomos.action;

import java.util.Locale;

/**
 * Describes the broad execution shape of a compiled spell action.
 */
public enum SpellActionType {
    UNKNOWN("unknown"),
    PROJECTILE("projectile"),
    STATIC_CONSTRUCT("construct"),
    DIRECT_EFFECT("direct_effect"),
    AREA_EFFECT("area_effect");

    private final String propertyValue;

    SpellActionType(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    /** Returns the stable value used by the legacy {@code action_type} property. */
    public String propertyValue() {
        return propertyValue;
    }

    /**
     * Resolves legacy property values and enum names. Unknown values map to
     * {@link #UNKNOWN} so custom properties remain forward compatible.
     */
    public static SpellActionType fromProperty(Object value) {
        if (value instanceof SpellActionType type) {
            return type;
        }
        if (value == null) {
            return UNKNOWN;
        }

        String normalized = String.valueOf(value).trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "projectile" -> PROJECTILE;
            case "construct", "static_construct", "static-construct" -> STATIC_CONSTRUCT;
            case "direct", "direct_effect", "direct-effect" -> DIRECT_EFFECT;
            case "area", "area_effect", "area-effect" -> AREA_EFFECT;
            default -> UNKNOWN;
        };
    }
}
