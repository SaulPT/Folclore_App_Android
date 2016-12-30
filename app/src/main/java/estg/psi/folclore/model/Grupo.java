package estg.psi.folclore.model;


import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Grupo {
    public int id, concelho_id, ativo;
    public String nome, abreviatura, logo;
    public Date data_criacao;

    public static List<Grupo> ordenar_nome(List<Grupo> grupos) {
        Collections.sort(grupos, new Comparator<Grupo>() {
            @Override
            public int compare(Grupo g1, Grupo g2) {
                if (g1.abreviatura.compareToIgnoreCase(g2.abreviatura) < 0) {
                    return -1;
                } else if (g1.abreviatura.compareToIgnoreCase(g2.abreviatura) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return grupos;
    }
}