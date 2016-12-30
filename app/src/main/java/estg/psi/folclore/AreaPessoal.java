package estg.psi.folclore;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.List;

import estg.psi.folclore.adapter.GruposAdapter;
import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Grupo;

public class AreaPessoal extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.listview_dados_api);
        viewstub.inflate();

        findViewById(R.id.textView_grupos_administrados).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        //VERIFICA SE O TELEMÓVEL TEM LIGAÇÃO À INTERNET
        if (!verificar_ligacao_internet()) {
            findViewById(R.id.loading_anim_listview).setVisibility(View.GONE);
        } else {
            //SE SIM, ACEDE À API
            Ion.with(this).load(API_URL + "user/grupos").setTimeout(TIMEOUT).addHeader("token", token)
                    .asJsonArray().withResponse().setCallback(new FutureCallback<Response<JsonArray>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonArray> result) {
                    //EM CASO DE ERRO NA LIGAÇÃO
                    if (e != null) {
                        Toast.makeText(AreaPessoal.this, "Erro na ligação ao servidor. A mostrar dados locais", Toast.LENGTH_SHORT).show();
                    } else {

                        //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                        if (result.getHeaders().code() != 200) {
                            //SE A API DESOLVEU ERRO
                            Toast.makeText(AreaPessoal.this, result.getHeaders().message() + ". A mostrar dados locais", Toast.LENGTH_SHORT).show();
                        } else {
                            CacheDB bd = new CacheDB(AreaPessoal.this);

                            //SE A API DEVOLVEU OS DADOS COM SUCESSO, DESERIALIZA E ATUALIZA A BD
                            GsonBuilder gson = new GsonBuilder();
                            gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                            List<Grupo> grupos = gson.create().fromJson(result.getResult(), new TypeToken<List<Grupo>>() {
                            }.getType());
                            bd.guardar_grupos(grupos);

                            //MOSTRA OS DADOS NA LISTVIEW
                            GruposAdapter grupos_adapter = new GruposAdapter(AreaPessoal.this, R.id.listview_dados_api, Grupo.ordenar_nome(grupos));
                            ((ListView) findViewById(R.id.listview_dados_api)).setAdapter(grupos_adapter);

                            bd.close();
                        }
                    }
                    findViewById(R.id.loading_anim_listview).setVisibility(View.GONE);
                }
            });
        }
    }
}
