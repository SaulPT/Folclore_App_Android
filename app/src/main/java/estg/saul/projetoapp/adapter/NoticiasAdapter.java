package estg.saul.projetoapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.List;

import estg.saul.projetoapp.Base;
import estg.saul.projetoapp.R;
import estg.saul.projetoapp.model.Noticia;


public class NoticiasAdapter extends ArrayAdapter<Noticia> {

    private Context context;
    private List<Noticia> noticias;

    public NoticiasAdapter(Context context, int layout, List<Noticia> noticias) {
        super(context, layout, noticias);
        this.noticias = noticias;
        this.context = context;
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Noticia noticia = noticias.get(position);

        if (convertView == null) {
            LayoutInflater layoutinflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutinflater.inflate(R.layout.noticias_list_view, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.text_noticia_titulo)).setText(noticia.titulo);
        ((TextView) convertView.findViewById(R.id.text_noticia_data)).setText(noticia.data_criacao);
        ((TextView) convertView.findViewById(R.id.text_noticia_conteudo)).setText(noticia.conteudo);

        Ion.with(getContext())
                .load(Base.IMG_URL + "noticias/" + noticia.imagem)
                .noCache()
                .withBitmap()
                .intoImageView((ImageView) convertView.findViewById(R.id.imagem_noticia));

        return convertView;
    }
}

