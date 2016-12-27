package estg.psi.folclore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;

import estg.psi.folclore.Base;
import estg.psi.folclore.R;
import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Noticia;


public class NoticiasAdapter extends ArrayAdapter<Noticia> {

    private final Context context;
    private final List<Noticia> noticias;

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
        ((TextView) convertView.findViewById(R.id.text_noticia_data)).setText(CacheDB.dateformat.format(noticia.data_criacao));
        TextView text_conteudo = (TextView) convertView.findViewById(R.id.text_noticia_conteudo);

        //VERIFICA SE O ANDROID É ANTES DO NOUGAT(7) PORQUE OS MÉTODOS DE PARSING DO HTML VARIAM
        if (Build.VERSION.SDK_INT >= 24) {
            text_conteudo.setText(Html.fromHtml(noticia.conteudo, Html.FROM_HTML_MODE_LEGACY));
        } else {
            text_conteudo.setText(Html.fromHtml(noticia.conteudo));
        }

        final ImageView imageview = (ImageView) convertView.findViewById(R.id.imagem_noticia);
        Ion.with(getContext())
                .load(Base.IMG_URL + "noticias/" + noticia.imagem)
                .setTimeout(1000)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (e != null) {
                            imageview.setImageResource(R.drawable.default_noticias);
                        } else {
                            imageview.setImageBitmap(result);
                        }
                    }
                });

        return convertView;
    }
}

