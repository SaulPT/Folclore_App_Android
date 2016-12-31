package estg.psi.folclore;

import android.os.Bundle;
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

import estg.psi.folclore.adapter.EventosAdapter;
import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Evento;

public class Eventos extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.listview_dados_api);
        viewstub.inflate();
    }

    @Override
    public void onResume() {
        super.onResume();

        loading(true);
        final CacheDB bd = new CacheDB(this);

        //VERIFICA SE HÁ LIGAÇÃO À INTERNET
        if (!verificar_ligacao_internet()) {
            mostrar_dados(bd.obter_eventos());
            bd.close();
            loading(false);
        } else {
            //SE SIM, ACEDE À API
            Ion.with(this).load(API_URL + "eventos").setTimeout(TIMEOUT).asJsonArray().withResponse().setCallback(new FutureCallback<Response<JsonArray>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonArray> result) {
                    //EM CASO DE ERRO NA LIGAÇÃO
                    if (e != null) {
                        Toast.makeText(Eventos.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        mostrar_dados(bd.obter_eventos());
                    } else {
                        //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                        if (result.getHeaders().code() != 200) {
                            //SE A API DESOLVEU ERRO
                            Toast.makeText(Eventos.this, "Erro do servidor (" + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            mostrar_dados(bd.obter_eventos());
                        } else {
                            //SE A API DEVOLVEU OS DADOS COM SUCESSO, DESERIALIZA E ATUALIZA A BD
                            GsonBuilder gson = new GsonBuilder();
                            gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                            List<Evento> eventos = gson.create().fromJson(result.getResult(), new TypeToken<List<Evento>>() {
                            }.getType());
                            bd.guardar_eventos(eventos);
                            mostrar_dados(eventos);
                        }
                    }
                    bd.close();
                    loading(false);
                }
            });
        }
    }

    private void mostrar_dados(List<Evento> eventos) {
        EventosAdapter eventos_adapter = new EventosAdapter(this, R.id.listview_dados_api, Evento.ordenar_data_desc(eventos));
        ((ListView) findViewById(R.id.listview_dados_api)).setAdapter(eventos_adapter);

    }

}
