package estg.psi.folclore.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import estg.psi.folclore.Base;
import estg.psi.folclore.R;


class BaseAdapter {

    //FUNCAO 'getView' GENÉRICA PARA APLICAR A OUTROS ADAPTERS
    static View getview_generico(Context context, View convertView, ViewGroup parent,
                                 String titulo, String data, String conteudo, String imagem, String api_suburl) {
        if (convertView == null) {
            LayoutInflater layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutinflater.inflate(R.layout.item_listview, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.titulo_nome)).setText(titulo);
        ((TextView) convertView.findViewById(R.id.data_local_concelho)).setText(data);
        TextView text_conteudo = (TextView) convertView.findViewById(R.id.conteudo_descricao);
        //PARA MOSTRAR OS DADOS DO CONTEÚDO QUE VÊM EM HTML
        //VERIFICA SE O ANDROID É ANTES DO NOUGAT(7) PORQUE OS MÉTODOS DE PARSING DO HTML VARIAM
        if (Build.VERSION.SDK_INT >= 24) {
            text_conteudo.setText(Html.fromHtml(conteudo, Html.FROM_HTML_MODE_LEGACY));
        } else {
            text_conteudo.setText(Html.fromHtml(conteudo));
        }

        //MOSTRA A IMAGEM
        final ImageView imageview = (ImageView) convertView.findViewById(R.id.imagem);
        Ion.with(context).load(Base.IMG_URL + api_suburl + imagem).setTimeout(Base.TIMEOUT).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (e != null) {
                    imageview.setImageResource(R.drawable.default_img);
                } else {
                    imageview.setImageBitmap(result);
                }
            }
        });

        return convertView;
    }

}

