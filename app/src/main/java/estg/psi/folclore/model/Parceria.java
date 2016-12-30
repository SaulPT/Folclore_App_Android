package estg.psi.folclore.model;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Parceria {
    public int id, ativo;
    public String parceiro, site_parceiro, descricao, imagem;

    public static List<Parceria> ordenar_parcerias_nome(List<Parceria> parcerias) {
        Collections.sort(parcerias, new Comparator<Parceria>() {
            @Override
            public int compare(Parceria p1, Parceria p2) {
                if (p1.parceiro.compareToIgnoreCase(p2.parceiro) < 0) {
                    return -1;
                } else if (p1.parceiro.compareToIgnoreCase(p2.parceiro) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return parcerias;
    }
}
