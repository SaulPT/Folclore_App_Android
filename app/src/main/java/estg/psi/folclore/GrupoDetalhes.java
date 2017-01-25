package estg.psi.folclore;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import estg.psi.folclore.adapter.GruposAdapter;
import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Grupo;

public class GrupoDetalhes extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.item_listview);
        viewstub.inflate();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (grupo_selecionado == -1) {
            onBackPressed();
        } else {
            final CacheDB bd = new CacheDB(this);
            loading(true);
            //VERIFICA SE HÁ LIGAÇÃO À INTERNET
            if (!verificar_ligacao_internet()) {
                //USA O ADAPTER DOS GRUPOS PARA MOSTRAR OS DADOS DO GRUPO OBTIDO
                this.setTitle(bd.obter_grupo(grupo_selecionado).abreviatura);
                GruposAdapter.mostrar_grupo(GrupoDetalhes.this, findViewById(android.R.id.content), bd.obter_grupo(grupo_selecionado), true);
                bd.close();
                loading(false);
            } else {
                //SE SIM, ACEDE À API
                Ion.with(this).load(API_URL + "grupos/" + grupo_selecionado).setTimeout(TIMEOUT).asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        //EM CASO DE ERRO NA LIGAÇÃO
                        if (e != null) {
                            Toast.makeText(GrupoDetalhes.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                            if (result.getHeaders().code() == 500) {
                                //SE A API DEVOLVEU UM ERRO CONHECIDO
                                Toast.makeText(GrupoDetalhes.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                bd.apagar_grupo(grupo_selecionado);
                                grupo_selecionado = -1;
                            } else if (result.getHeaders().code() != 200) {
                                //SE A API DEVOLVEU UM ERRO DESCONHECIDO
                                Toast.makeText(GrupoDetalhes.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            } else {
                                //SE A API DEVOLVEU OS DADOS COM SUCESSO, DESERIALIZA E ATUALIZA A BD
                                GsonBuilder gson = new GsonBuilder();
                                gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                                Grupo grupo = gson.create().fromJson(result.getResult(), Grupo.class);
                                bd.guardar_grupo(grupo);
                            }
                        }

                        //USA O ADAPTER DOS GRUPOS PARA MOSTRAR OS DADOS DO GRUPO OBTIDO
                        if (grupo_selecionado != -1) {
                            GrupoDetalhes.this.setTitle(bd.obter_grupo(grupo_selecionado).abreviatura);
                            GruposAdapter.mostrar_grupo(GrupoDetalhes.this, findViewById(android.R.id.content), bd.obter_grupo(grupo_selecionado), true);
                        } else {
                            onBackPressed();
                        }
                        bd.close();
                        loading(false);
                    }
                });
            }
        }
    }

    private void loading(boolean loading) {
        if (loading) {
            findViewById(R.id.loading_anim_item).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_info_item).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_anim_item).setVisibility(View.GONE);
            findViewById(R.id.layout_info_item).setVisibility(View.VISIBLE);
        }
    }

}
