package estg.psi.folclore.model;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Concelho {
    public int id, distrito_id;
    public String nome;

    public static List<Concelho> ordenar_nome(List<Concelho> concelhos) {
        Collections.sort(concelhos, new Comparator<Concelho>() {
            @Override
            public int compare(Concelho c1, Concelho c2) {
                if (c1.nome.compareToIgnoreCase(c2.nome) < 0) {
                    return -1;
                } else if (c1.nome.compareToIgnoreCase(c2.nome) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return concelhos;
    }

    @Override
    public String toString() {
        return nome;
    }
}
