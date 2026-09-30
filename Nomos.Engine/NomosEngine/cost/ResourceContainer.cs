using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;

namespace NomosEngine.Cost
{
    public class ResourceContainer
    {
        private ConcurrentDictionary<string, double> resources =
            new ConcurrentDictionary<string, double>();

        public void Set(string resource, double amount)
        {
            if (Valid(resource, amount)) return;

            resources[Normalize(resource)] = System.Math.Max(amount, 0);
        }

        public bool CanAfford(SpellCost cost) => CanAfford(cost.GetAllCosts());

        private bool CanAfford(IDictionary<string, double> costs)
        {
            foreach (var entry in costs)
            {
                if (resources.TryGetValue(entry.Key, out double value) && value < entry.Value)
                {
                    return false;
                }
            }

            return true;
        }

        public bool TryConsume(SpellCost cost)
        {
            var costs =
                cost == null ? new Dictionary<string, double>() : cost.GetAllCosts();
            if (!CanAfford(costs)) return false;

            foreach (var entry in costs)
            {
                resources[entry.Key] = resources.TryGetValue(entry.Key, out double value)
                    ? value - entry.Value
                    : -entry.Value;
            }

            return true;
        }

        public void Consume(SpellCost cost)
        {
            if (!TryConsume(cost)) throw new InvalidOperationException("Cannot afford cost");
        }

        public double Get(string resource) => string.IsNullOrEmpty(resource) ? 0.0 :
            resources.TryGetValue(Normalize(resource), out double value) ? value : 0.0;

        public IReadOnlyDictionary<string, double> GetAll() => resources;

        private static bool Valid(string resource, double amount)
        {
            return string.IsNullOrEmpty(resource) || double.IsInfinity(amount);
        }

        private static string Normalize(string value)
        {
            return value?.Trim().ToLower();
        }

        public override string ToString() =>
            string.Join(", ", resources.Select(x => $"{x.Key}: {x.Value}"));
    }
}