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

import estg.psi.folclore.adapter.ParceriasAdapter;
import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Parceria;

public class Parcerias extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.listview_dados_api);
        viewstub.inflate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loading_listview(true);
        final CacheDB bd = new CacheDB(this);

        //VERIFICA SE HÁ LIGAÇÃO À INTERNET
        if (!verificar_ligacao_internet()) {
            mostrar_dados(bd.obter_parcerias());
            bd.close();
            loading_listview(false);
        } else {
            //SE SIM, ACEDE À API
            Ion.with(this).load(API_URL + "parcerias").setTimeout(TIMEOUT).asJsonArray().withResponse().setCallback(new FutureCallback<Response<JsonArray>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonArray> result) {
                    //EM CASO DE ERRO NA LIGAÇÃO
                    if (e != null) {
                        Toast.makeText(Parcerias.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        mostrar_dados(bd.obter_parcerias());
                    } else {
                        //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                        if (result.getHeaders().code() != 200) {
                            //SE A API DEVOLVEU ERRO
                            Toast.makeText(Parcerias.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            mostrar_dados(bd.obter_parcerias());
                        } else {
                            //SE A API DEVOLVEU OS DADOS COM SUCESSO, DESERIALIZA E ATUALIZA A BD
                            GsonBuilder gson = new GsonBuilder();
                            gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                            List<Parceria> parcerias = gson.create().fromJson(result.getResult(), new TypeToken<List<Parceria>>() {
                            }.getType());
                            bd.guardar_parcerias(parcerias);
                            mostrar_dados(parcerias);
                        }
                    }
                    bd.close();
                    loading_listview(false);
                }
            });
        }
    }

    private void mostrar_dados(List<Parceria> parcerias) {
        ParceriasAdapter parcerias_adapter = new ParceriasAdapter(this, R.id.listview_dados_api, Parceria.ordenar_parcerias_nome(parcerias));
        ((ListView) findViewById(R.id.listview_dados_api)).setAdapter(parcerias_adapter);
    }

}
