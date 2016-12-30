package estg.psi.folclore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.List;

import estg.psi.folclore.Base;
import estg.psi.folclore.R;
import estg.psi.folclore.model.Concelho;
import estg.psi.folclore.model.Grupo;


public class GruposAdapter extends ArrayAdapter<Grupo> {

    private final Context context;
    private final List<Grupo> grupos;

    public GruposAdapter(Context context, int layout, List<Grupo> grupos) {
        super(context, layout, grupos);
        this.grupos = grupos;
        this.context = context;
    }

    public static void mostrar_grupo(Context context, View view, Grupo grupo, boolean vista_detalhe) {
        ((TextView) view.findViewById(R.id.titulo_nome)).setText(grupo.abreviatura);

        final TextView textview_concelho = (TextView) view.findViewById(R.id.data_local_concelho);
        //MOSTRAR O CONCELHO
        Ion.with(context).load(Base.API_URL + "concelhos/" + grupo.concelho_id).setTimeout(Base.TIMEOUT).asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                if (e != null) {
                    textview_concelho.setText("Concelho");
                } else {
                    Concelho concelho = new Gson().fromJson(result.getResult(), Concelho.class);
                    textview_concelho.setText(concelho.nome);
                }
            }
        });

        final ImageView imageview = (ImageView) view.findViewById(R.id.imagem);
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("mostrar_imagens_lista_grupos", false)) {
            //MOSTRA A IMAGEM
            Ion.with(context).load(Base.IMG_URL + "grupos/" + grupo.logo).setTimeout(Base.TIMEOUT).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    if (e != null) {
                        imageview.setImageResource(R.drawable.default_noticias);
                    } else {
                        imageview.setImageBitmap(result);
                    }
                }
            });
        } else {
            imageview.setVisibility(View.GONE);
        }

        TextView textView_nome_completo = (TextView) view.findViewById(R.id.conteudo_descricao);
        if (vista_detalhe) {
            textView_nome_completo.setText(grupo.nome);
        } else {
            textView_nome_completo.setVisibility(View.GONE);
        }
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Grupo grupo = grupos.get(position);

        if (convertView == null) {
            LayoutInflater layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutinflater.inflate(R.layout.item_listview, parent, false);
        }

        mostrar_grupo(context, convertView, grupo, false);

        return convertView;
    }
}

