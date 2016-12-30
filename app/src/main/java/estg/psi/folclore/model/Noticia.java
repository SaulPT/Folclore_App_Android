package estg.psi.folclore.model;


import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Noticia {
    public int id, autor_id, ativo, aprovado;
    public String titulo, conteudo, imagem;
    public Date data_criacao, data_edicao;

    public static List<Noticia> ordenar_noticias_data_desc(List<Noticia> noticias) {
        Collections.sort(noticias, new Comparator<Noticia>() {
            @Override
            public int compare(Noticia n1, Noticia n2) {
                if (n1.data_edicao.after(n2.data_edicao)) {
                    return -1;
                } else if (n1.data_edicao.before(n2.data_edicao)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return noticias;
    }
}
