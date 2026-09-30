using NomosEngine.Cost;

namespace NomosEngine
{
    public class SpellCaster
    {
        private readonly string name;
        private readonly ResourceContainer resource = new ResourceContainer();

        public SpellCaster(string name)
        {
            this.name = string.IsNullOrEmpty(name) ? "unknown" : name;
        }
        
        public string Name() => name;
        public ResourceContainer Resources() => resource;
        
        public string ToString() => "SpellCaster{ name=" + name + "\'" + ", resources=" + resource.ToString() + "}";
    }
}