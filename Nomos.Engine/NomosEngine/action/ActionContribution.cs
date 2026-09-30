using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using NomosEngine.Condition;

namespace NomosEngine.Action
{
    public class ActionContribution
    {
        private readonly ConcurrentDictionary<string, object> properties =
            new ConcurrentDictionary<string, object>();

        private readonly ConcurrentDictionary<string, double> addedNumbers =
            new ConcurrentDictionary<string, double>();

        private readonly ConcurrentDictionary<string, double> setNumbers =
            new ConcurrentDictionary<string, double>();

        private readonly ConcurrentDictionary<string, double> addedCosts =
            new ConcurrentDictionary<string, double>();

        private readonly ConcurrentDictionary<string, double> costMultipliers =
            new ConcurrentDictionary<string, double>();


        public ActionContribution AddContribution(SpellCondition condition)
        {
            return null;
        }

        public IDictionary<string, object> Properties() =>
            new ReadOnlyDictionary<string, object>(properties);

        public IDictionary<string, double> AddedNumbers() =>
            new ReadOnlyDictionary<string, double>(addedNumbers);

        public IDictionary<string, double> SetNumbers() =>
            new ReadOnlyDictionary<string, double>(setNumbers);

        public IDictionary<string, double> AddedCosts() =>
            new ReadOnlyDictionary<string, double>(addedCosts);

        public IDictionary<string, double> CostMultipliers() =>
            new ReadOnlyDictionary<string, double>(costMultipliers);

        private static string requireKey(string key)
        {
            if (string.IsNullOrEmpty(key))
                throw new System.ArgumentException("key must not be null or empty");

            return key?.Trim().ToLower();
        }

        private static void requireFinite(double value)
        {
            if (double.IsInfinity(value))
                throw new System.ArgumentException("value must be finite");
        }
    }
}