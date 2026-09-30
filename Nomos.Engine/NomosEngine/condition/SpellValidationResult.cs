using System;
using System.Collections.Generic;

namespace NomosEngine.Condition
{
    public class SpellValidationResult
    {
        private List<string> errors = new List<string>();

        public void AddError(string error)
        {
            if(!string.IsNullOrEmpty(error))
            {
                errors.Add(error);
            }
        }
        
        public bool IsValid() => errors.Count == 0;

        public IReadOnlyList<string> Errors => errors;
    }
}