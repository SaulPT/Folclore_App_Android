package estg.psi.folclore;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Concelho;
import estg.psi.folclore.model.Grupo;

public class EditarGrupoDetalhes extends AppCompatActivity {

    String[] distritos;
    Concelho[] concelhos;
    Grupo grupo;
    int grupo_id, grupo_distrito_id;
    EditText grupo_nome, grupo_abreviatura;
    Spinner grupo_concelho, grupo_distrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CONFIGURA A VIEW NORMAL, MAS SEM O NAVIGATION DRAWER, PARA CONTER SÓ UMA SIMPLES TOOLBAR
        //COM UM BOTAO DE VOLTAR E AS OPÇÕES DE EDIÇÃO
        setContentView(R.layout.home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_ab_back_material);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.editar_grupo_detalhes);
        viewstub.inflate();


        grupo_nome = (EditText) findViewById(R.id.editText_grupo_nome);
        grupo_abreviatura = (EditText) findViewById(R.id.editText_grupo_abreviatura);
        grupo_distrito = (Spinner) findViewById(R.id.spinner_grupo_distrito);
        grupo_concelho = (Spinner) findViewById(R.id.spinner_grupo_concelho);


        grupo_distrito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //MOSTRA OS CONCELHOS DO DISTRITO SELECIONADO
                List<Concelho> concelhos_distrito = new ArrayList<>();
                for (Concelho concelho : concelhos) {
                    if (concelho.distrito_id == (int) id + 1) {
                        concelhos_distrito.add(concelho);
                    }
                }
                Concelho.ordenar_nome(concelhos_distrito);
                grupo_concelho.setAdapter(new ArrayAdapter<>(EditarGrupoDetalhes.this, R.layout.item_spinner, concelhos_distrito));

                //SELECIONA O CONCELHO DO GRUPO AO MOSTRAR O DISTRITO DESSE GRUPO
                if (grupo_distrito_id == id + 1) {
                    int posicao_concelho_grupo = 0;
                    for (int x = 0; x < concelhos_distrito.size(); x++) {
                        if (concelhos_distrito.get(x).id == grupo.concelho_id) {
                            posicao_concelho_grupo = x;
                        }
                    }
                    grupo_concelho.setSelection(posicao_concelho_grupo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        menu.findItem(R.id.action_edit_adicionar).setVisible(false);
        menu.findItem(R.id.action_edit_eliminar).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_guardar) {
            //CRIA O NOVO GRUPO PASA SER ATUALIZADO
            Grupo grupo_atualizado = grupo;
            grupo_atualizado.abreviatura = grupo_abreviatura.getText().toString();
            grupo_atualizado.nome = grupo_nome.getText().toString();
            if (concelhos != null) {
                grupo_atualizado.concelho_id = ((Concelho) grupo_concelho.getItemAtPosition(grupo_concelho.getSelectedItemPosition())).id;
            }

            //ENVIA O NOVO GRUPO PARA A API
            loading(true);
            if (!verificar_ligacao_internet()) {
                Toast.makeText(this, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
                loading(false);
            } else {
                Ion.with(this).load("PUT", Base.API_URL + "grupos/" + grupo_atualizado.id).setTimeout(Base.TIMEOUT)
                        .addHeader("token", getIntent().getStringExtra("token"))
                        .setStringBody(new GsonBuilder().setDateFormat(CacheDB.DATE_TIME_FORMAT).create().toJson(grupo_atualizado))
                        .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                            Toast.makeText(EditarGrupoDetalhes.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                            loading(false);
                        } else {
                            if (result.getHeaders().code() != 200) {
                                Toast.makeText(EditarGrupoDetalhes.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                                loading(false);
                            } else {
                                onBackPressed();
                            }
                        }
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        grupo_id = getIntent().getIntExtra("grupo_id", -1);

        //CARREGA OS DADOS DO GRUPO, COMEÇANDO PELOS DISTRITOS
        loading(true);
        if (!verificar_ligacao_internet()) {
            Toast.makeText(this, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
            CacheDB bd = new CacheDB(this);
            grupo = bd.obter_grupo(grupo_id);
            bd.close();
            if (grupo != null) {
                mostrar_grupo();
            } else {
                onBackPressed();
            }
        } else {
            Ion.with(this).load(Base.API_URL + "distritos").setTimeout(Base.TIMEOUT).asJsonArray().withResponse().setCallback(new FutureCallback<Response<JsonArray>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonArray> result) {
                    if (e != null) {
                        Toast.makeText(EditarGrupoDetalhes.this, "Não foi possível obter distritos e concelhos", Toast.LENGTH_SHORT).show();
                        carregar_grupo();
                    } else {
                        if (result.getHeaders().code() != 200) {
                            Toast.makeText(EditarGrupoDetalhes.this, "Não foi possível obter distritos e concelhos", Toast.LENGTH_SHORT).show();
                            carregar_grupo();
                        } else {
                            distritos = new Gson().fromJson(result.getResult(), String[].class);
                            carregar_concelhos();
                        }
                    }
                }
            });
        }
    }

    private void carregar_concelhos() {
        Ion.with(this).load(Base.API_URL + "concelhos").setTimeout(Base.TIMEOUT).asJsonArray().withResponse().setCallback(new FutureCallback<Response<JsonArray>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonArray> result) {
                if (e != null) {
                    Toast.makeText(EditarGrupoDetalhes.this, "Não foi possível obter distritos e concelhos", Toast.LENGTH_SHORT).show();
                    carregar_grupo();
                } else {
                    if (result.getHeaders().code() != 200) {
                        Toast.makeText(EditarGrupoDetalhes.this, "Não foi possível obter distritos e concelhos", Toast.LENGTH_SHORT).show();
                        carregar_grupo();
                    } else {
                        concelhos = new Gson().fromJson(result.getResult(), Concelho[].class);
                        carregar_grupo();
                    }
                }
            }
        });
    }

    private void carregar_grupo() {
        //CARREGA AS INFORMAÇÕES
        Ion.with(this).load(Base.API_URL + "grupos/" + grupo_id).setTimeout(Base.TIMEOUT).asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                CacheDB bd = new CacheDB(EditarGrupoDetalhes.this);
                if (e != null) {
                    Toast.makeText(EditarGrupoDetalhes.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                    grupo = bd.obter_grupo(grupo_id);
                } else {
                    if (result.getHeaders().code() == 500) {
                        Toast.makeText(EditarGrupoDetalhes.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                        grupo = null;
                        bd.apagar_grupo(grupo_id);
                    } else if (result.getHeaders().code() != 200) {
                        Toast.makeText(EditarGrupoDetalhes.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                        grupo = bd.obter_grupo(grupo_id);
                    } else {
                        GsonBuilder gson = new GsonBuilder();
                        gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                        grupo = gson.create().fromJson(result.getResult(), Grupo.class);
                        bd.guardar_grupo(grupo);
                    }
                }
                bd.close();
                if (grupo != null) {
                    mostrar_grupo();
                } else {
                    onBackPressed();
                }

            }
        });
    }

    private void mostrar_grupo() {
        this.setTitle(grupo.abreviatura);

        //CARREGA A IMAGEM
        final ImageView imageview = (ImageView) findViewById(R.id.grupo_imagem);
        Ion.with(this).load(Base.IMG_URL + "grupos/" + grupo.logo).setTimeout(Base.TIMEOUT).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (e != null) {
                    imageview.setImageResource(R.drawable.default_noticias);
                } else {
                    imageview.setImageBitmap(result);
                }
            }
        });

        grupo_abreviatura.setText(grupo.abreviatura);
        grupo_nome.setText(grupo.nome);
        if (distritos != null) {
            grupo_distrito.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, distritos));

            //PARA SELECIONAR O DISTRITO DO GRUPO
            int x = 0;
            grupo_distrito_id = -1;
            do {
                if (concelhos[x].id == grupo.concelho_id) {
                    grupo_distrito_id = concelhos[x].distrito_id;
                }
                x++;
            } while (grupo_distrito_id == -1);

            grupo_distrito.setSelection(grupo_distrito_id - 1);
        }

        loading(false);
    }

    private void loading(boolean loading) {
        if (loading) {
            findViewById(R.id.loading_anim_grupo_detalhes).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_editar_detalhes_info).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_anim_grupo_detalhes).setVisibility(View.GONE);
            findViewById(R.id.layout_editar_detalhes_info).setVisibility(View.VISIBLE);
        }
    }

    private boolean verificar_ligacao_internet() {
        ConnectivityManager net = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return !(net.getActiveNetworkInfo() == null || !net.getActiveNetworkInfo().isConnectedOrConnecting());
    }

}
