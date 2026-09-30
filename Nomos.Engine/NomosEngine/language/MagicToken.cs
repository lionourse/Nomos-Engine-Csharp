using NomosEngine.Semantic;

namespace NomosEngine.Language
{
    public abstract class MagicToken
    {
        public MagicMeaning Meaning()
        {
            return new MagicMeaning();
        }

        public bool IsNumeric() => false;

        public bool AcceptsValue() => false;
    }
}