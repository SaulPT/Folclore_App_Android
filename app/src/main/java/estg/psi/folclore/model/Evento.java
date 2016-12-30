package estg.psi.folclore.model;


import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Evento {
    public int id, autor_id, concelho_id, estado;
    public String nome, descricao, local, imagem;
    public Date data, data_criacao;

    public static List<Evento> ordenar_data_desc(List<Evento> eventos) {
        Collections.sort(eventos, new Comparator<Evento>() {
            @Override
            public int compare(Evento e1, Evento e2) {
                if (e1.data.after(e2.data)) {
                    return 1;
                } else if (e1.data.before(e2.data)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return eventos;
    }
}
