using System;
using System.Threading;

namespace NomosEngine.Utils
{
    public class AtomicReference<T> where T : class
    {
        private T value;

        public AtomicReference(T initialValue)
        {
            value = initialValue;
        }

        public AtomicReference()
        {
            value = null;
        }

        public T Get()
        {
            return value;
        }

        public void Set(T newValue)
        {
            Interlocked.Exchange(ref value, newValue);
        }

        public T UpdateAndGet(Func<T, T> updateFunction)
        {
            T newValue = updateFunction(value);
            Interlocked.Exchange(ref value, newValue);
            return newValue;
        }

        public bool CompareAndSet(T expected, T update)
        {
            T previous = Interlocked.CompareExchange(
                ref value, update, expected);

            return ReferenceEquals(previous, expected);
        }

        public override string ToString()
        {
            return "AtomicReference<" + typeof(T).Name + "> (" + value + ")";
        }
    }
}