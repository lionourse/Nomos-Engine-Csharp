using System;
using System.ComponentModel;

namespace NomosEngine.Attributes
{
    [AttributeUsage(AttributeTargets.Class, AllowMultiple = false, Inherited = false)]
    public sealed class MagicTokenAttribute : Attribute
    {
        public string Id { get; }
        public string[] Aliases { get; }

        public MagicTokenAttribute(string id, params string[] aliases)
        {
            Id = id;
            Aliases = aliases;
        }
    }
}