using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace NomosEngine.Cost
{
    public class SpellCost
    {
        private ConcurrentDictionary<string, double> costs =
            new ConcurrentDictionary<string, double>();

        public void Add(string resource, double value)
        {
            if (string.IsNullOrEmpty(resource) || double.IsInfinity(value) || double.IsNaN(value))
                throw new ArgumentException("Invalid resource or value");
            string key = Normalize(resource);
            double result = costs.TryGetValue(key, out double oldValue)
                ? oldValue + System.Math.Max(value, 0.0)
                : System.Math.Max(value, 0.0);
            costs.TryAdd(key, result);
        }

        public double Get(string resource) => string.IsNullOrEmpty(resource) ? 0.0 :
            costs.TryGetValue(Normalize(resource), out double value) ? value : 0.0;

        public bool isEmpty() => costs.Count == 0;

        public IDictionary<string, double> GetAllCosts() => new ReadOnlyDictionary<string, double>(costs);
        
        private static string Normalize(string value) => value?.Trim().ToLower();

        public override string ToString() => string.Join(", ", costs);
    }
}