using System;
using System.ComponentModel;
using NomosEngine.Utils;

namespace NomosEngine
{
    public class Nomos
    {
        private static readonly AtomicReference<Nomos> INSTANCE = new AtomicReference<Nomos>();

        public static Nomos init() {
            Nomos created = new Nomos();
            if (!INSTANCE.CompareAndSet(created, null)) {
                throw new InvalidAsynchronousStateException("Nomos is already initialized.");
            }
            return created;
        }
    }
}