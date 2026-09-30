namespace NomosEngine.Condition
{
    public interface SpellCondition
    {


    }
    public interface ContextualValidator
    {
        void Validate(SpellCaster caster, SpellValidationResult validation);
    }
}