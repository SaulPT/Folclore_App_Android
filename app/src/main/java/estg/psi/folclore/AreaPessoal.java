package estg.psi.folclore;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.listview_dados_api);
        viewstub.inflate();

        final ListView listview_grupos = (ListView) findViewById(R.id.listview_dados_api);
        findViewById(R.id.textView_grupos_administrados).setVisibility(View.VISIBLE);

        listview_grupos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                //OBTEM O ID E O NOME DO GRUPO
                CacheDB bd = new CacheDB(AreaPessoal.this);
                Grupo grupo_selecionado = (Grupo) listview_grupos.getAdapter().getItem(position);
                bd.close();
                final int grupo_id = grupo_selecionado.id;
                final String grupo_abreviatura = grupo_selecionado.abreviatura;

                new AlertDialog.Builder(AreaPessoal.this)
                        .setTitle(grupo_abreviatura)
                        .setNegativeButton(R.string.cancel, null)
                        .setItems(R.array.popup_group_actions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intente = new Intent();
                                switch (which) {
                                    case 0:
                                        intente.setAction("estg.psi.folclore.EDITARGRUPODETALHES");
                                        break;
                                    case 1:
                                        intente.setAction("estg.psi.folclore.EDITARGRUPOHISTORIAL");
                                        intente.putExtra("grupo_nome", grupo_abreviatura);
                                        break;
                                    case 2:
                                        intente.setAction("estg.psi.folclore.EDITARGRUPOCORPOGERENTE");
                                        intente.putExtra("grupo_nome", grupo_abreviatura);
                                        break;
                                    case 3:
                                        intente.setAction("estg.psi.folclore.EDITARGRUPOCONTACTO");
                                        intente.putExtra("grupo_nome", grupo_abreviatura);
                                        break;
                                }
                                intente.putExtra("grupo_id", grupo_id);
                                intente.putExtra("token", token);
                                startActivity(intente);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TERMINA A ATIVIDADE SE NÃO EXISTIR UM UTILIZADOR AUTENTICADO
        if (!logado) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //VERIFICA SE HÁ LIGAÇÃO À INTERNET
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
                        Toast.makeText(AreaPessoal.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                    } else {

                        //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                        if (result.getHeaders().code() != 200) {
                            //SE A API DEVOLVEU ERRO
                            Toast.makeText(AreaPessoal.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
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
