package entity;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public enum TipologiaEvento {
    ESIBIZIONE((ic, is) -> ic.size() == 1 && !is.isEmpty()), // 1  cantante, 1+ strumentisti
    CONCERTO((ic, is) -> !ic.isEmpty() && !is.isEmpty()),    // 1+ cantanti, 1+ strumentisti
    STRUMENTALE((ic, is) -> ic.isEmpty() && !is.isEmpty());  // 0  cantanti, 1+ strumentisti

    private final BiPredicate<Set<Cantante>, Set<Strumentista>> accettabilita;

    TipologiaEvento(BiPredicate<Set<Cantante>, Set<Strumentista>> accettabilita) {
        this.accettabilita = accettabilita;
    }

    public boolean verifica(Collection<Musicista> musicisti) {
        Set<Cantante> cantanti = musicisti.stream().filter(m -> m instanceof Cantante).map(m -> (Cantante)m).collect(Collectors.toSet());
        Set<Strumentista> strumentisti = musicisti.stream().filter(m -> m instanceof Strumentista).map(m -> (Strumentista)m).collect(Collectors.toSet());

        return accettabilita.test(cantanti, strumentisti);
    }
}
