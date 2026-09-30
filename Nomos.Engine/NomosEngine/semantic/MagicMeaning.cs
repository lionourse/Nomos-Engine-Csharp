using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace NomosEngine.Semantic
{
    public class MagicMeaning
    {
        private ConcurrentDictionary<string, double> dimensions =
            new ConcurrentDictionary<string, double>();
        
        public MagicMeaning add(string dimension, double value)
        {
            string normalized = normalizeRequired(dimension, "dimension");
            requireFinite(value, "value");
            dimensions[dimension] = value;
            return this;
        }

        public IReadOnlyDictionary<string, double> GetDimensions()
        {
            var sorted = new SortedDictionary<string, double>(StringComparer.Ordinal);

            foreach (var dimension in dimensions)
            {
                sorted.Add(dimension.Key, dimension.Value);
            }

            return new ReadOnlyDictionary<string, double>(sorted);
        }


        private static string normalizeRequired(string value, string label)
        {
            return string.IsNullOrEmpty(value)
                ? throw new ArgumentNullException(label + "cannot be null or empty")
                : value.ToLower();
        }

        private static void requireFinite(double value, string label)
        {
            if (double.IsInfinity(value) || double.IsNaN(value))
            {
                throw new ArgumentException(label + "must be finite");
            }
        }
    }
}