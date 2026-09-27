package fr.olympus.nomos.cast;

import fr.olympus.nomos.cost.ResourceContainer;

/** A named spell caster and its thread-safe resource container. */
public class SpellCaster {
    private final String name;
    private final ResourceContainer resources = new ResourceContainer();

    public SpellCaster(String name) {
        this.name = name == null ? "unknown" : name;
    }

    public String getName() { return name; }
    public ResourceContainer getResources() { return resources; }

    @Override
    public String toString() {
        return "SpellCaster{" + "name='" + name + '\'' + ", resources=" + resources + '}';
    }
}
