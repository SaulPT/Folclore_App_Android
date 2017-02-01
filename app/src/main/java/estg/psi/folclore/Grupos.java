package estg.psi.folclore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
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

public class Grupos extends Base {

    private Button button_remover_grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.listview_dados_api);
        viewstub.inflate();

        final SharedPreferences definicoes = PreferenceManager.getDefaultSharedPreferences(this);

        final ListView listview_grupos = (ListView) findViewById(R.id.listview_dados_api);
        listview_grupos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CacheDB bd = new CacheDB(Grupos.this);
                grupo_selecionado = ((Grupo) listview_grupos.getAdapter().getItem(position)).id;
                bd.close();
                definicoes.edit().putInt("grupo_selecionado", grupo_selecionado).apply();
                Intent intente = new Intent("estg.psi.folclore.GRUPODETALHES");
                iniciar_intente_extras(intente);
            }
        });

        button_remover_grupo = (Button) findViewById(R.id.button_remover_grupo_selecionado);
        button_remover_grupo.setVisibility(View.VISIBLE);
        button_remover_grupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grupo_selecionado = -1;
                definicoes.edit().remove("grupo_selecionado").apply();
                atualizar_nav_header_action_menu();
                v.setEnabled(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (grupo_selecionado == -1) {
            button_remover_grupo.setEnabled(false);
        } else {
            button_remover_grupo.setEnabled(true);
        }

        loading_listview(true);
        final CacheDB bd = new CacheDB(this);

        //VERIFICA SE HÁ LIGAÇÃO À INTERNET
        if (!verificar_ligacao_internet()) {
            mostrar_dados(bd.obter_grupos());
            bd.close();
            loading_listview(false);
        } else {
            //SE SIM, ACEDE À API
            Ion.with(this).load(API_URL + "grupos").setTimeout(TIMEOUT).asJsonArray().withResponse().setCallback(new FutureCallback<Response<JsonArray>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonArray> result) {
                    //EM CASO DE ERRO NA LIGAÇÃO
                    if (e != null) {
                        Toast.makeText(Grupos.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        mostrar_dados(bd.obter_grupos());
                    } else {
                        //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                        if (result.getHeaders().code() != 200) {
                            //SE A API DEVOLVEU ERRO
                            Toast.makeText(Grupos.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            mostrar_dados(bd.obter_grupos());
                        } else {
                            //SE A API DEVOLVEU OS DADOS COM SUCESSO, DESERIALIZA E ATUALIZA A BD
                            GsonBuilder gson = new GsonBuilder();
                            gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                            List<Grupo> grupos = gson.create().fromJson(result.getResult(), new TypeToken<List<Grupo>>() {
                            }.getType());
                            bd.apagar_grupos();
                            bd.guardar_grupos(grupos);
                            mostrar_dados(grupos);
                        }
                    }
                    bd.close();
                    loading_listview(false);
                }
            });
        }
    }

    private void mostrar_dados(List<Grupo> grupos) {
        GruposAdapter grupos_adapter = new GruposAdapter(this, R.id.listview_dados_api, Grupo.ordenar_nome(grupos));
        ((ListView) findViewById(R.id.listview_dados_api)).setAdapter(grupos_adapter);
    }

}
