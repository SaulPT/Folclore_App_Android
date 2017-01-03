package estg.psi.folclore;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;

import java.util.Calendar;

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Historial;

public class EditarGrupoHistorial extends AppCompatActivity {

    private Historial historial;
    private int grupo_id;
    private EditText grupo_historial;
    private boolean criar_novo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(getIntent().getStringExtra("grupo_nome"));

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
        viewstub.setLayoutResource(R.layout.editar_grupo_info);
        viewstub.inflate();


        grupo_historial = (EditText) findViewById(R.id.editText_grupo_info);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        if (historial == null) {
            menu.findItem(R.id.action_edit_adicionar).setVisible(true);
            menu.findItem(R.id.action_edit_eliminar).setVisible(false);
            menu.findItem(R.id.action_edit_guardar).setVisible(false);

        } else if (criar_novo) {
            menu.findItem(R.id.action_edit_adicionar).setVisible(false);
            menu.findItem(R.id.action_edit_eliminar).setVisible(false);
            menu.findItem(R.id.action_edit_guardar).setVisible(true);

        } else {
            menu.findItem(R.id.action_edit_adicionar).setVisible(false);
            menu.findItem(R.id.action_edit_eliminar).setVisible(true);
            menu.findItem(R.id.action_edit_guardar).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_adicionar:
                criar_novo = true;
                historial = new Historial();
                mostrar_historial();
                break;
            case R.id.action_edit_guardar:
                //CRIA O NOVO HISTORIAL
                final Historial novo_historial = historial;
                novo_historial.grupo_id = grupo_id;
                novo_historial.historial = grupo_historial.getText().toString();
                novo_historial.data_edicao = Calendar.getInstance().getTime();

                //SELECIONA O PEDIDO CONFORME SEJA ALTERAÇÃO OU CRIAÇÃO
                Builders.Any.B ion_load;
                if (criar_novo) {
                    novo_historial.data_criacao = Calendar.getInstance().getTime();
                    ion_load = Ion.with(this).load("POST", Base.API_URL + "grupohistorials");
                } else {
                    ion_load = Ion.with(this).load("PUT", Base.API_URL + "grupohistorials/" + grupo_id);
                }

                //ENVIA O NOVO HISTORIAL PARA A API
                loading(true);
                if (!verificar_ligacao_internet()) {
                    Toast.makeText(this, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
                    loading(false);
                } else {
                    ion_load.setTimeout(Base.TIMEOUT).addHeader("token", getIntent().getStringExtra("token"))
                            .setStringBody(new GsonBuilder().setDateFormat(CacheDB.DATE_TIME_FORMAT).create().toJson(novo_historial))
                            .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e != null) {
                                Toast.makeText(EditarGrupoHistorial.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                                loading(false);
                            } else {
                                if (result.getHeaders().code() == 500) {
                                    Toast.makeText(EditarGrupoHistorial.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                    loading(false);
                                } else if (result.getHeaders().code() != 200) {
                                    Toast.makeText(EditarGrupoHistorial.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                                    loading(false);
                                } else {
                                    //EM CASO DE SUCESSO, ATUALIZA A BD E SAI DA ATIVIDADE
                                    CacheDB bd = new CacheDB(EditarGrupoHistorial.this);
                                    bd.guardar_historial(novo_historial);
                                    bd.close();
                                    onBackPressed();
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.action_edit_eliminar:
                loading(true);
                if (!verificar_ligacao_internet()) {
                    Toast.makeText(this, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
                    loading(false);
                } else {
                    Ion.with(this).load("DELETE", Base.API_URL + "grupohistorials/" + grupo_id).setTimeout(Base.TIMEOUT)
                            .addHeader("token", getIntent().getStringExtra("token"))
                            .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e != null) {
                                Toast.makeText(EditarGrupoHistorial.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                                loading(false);
                            } else {
                                if (result.getHeaders().code() == 500) {
                                    Toast.makeText(EditarGrupoHistorial.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                    loading(false);
                                } else if (result.getHeaders().code() != 200) {
                                    Toast.makeText(EditarGrupoHistorial.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                                    loading(false);
                                } else {
                                    //EM CASO DE SUCESSO, ATUALIZA A BD E SAI DA ATIVIDADE
                                    CacheDB bd = new CacheDB(EditarGrupoHistorial.this);
                                    bd.apagar_historial(grupo_id);
                                    bd.close();
                                    onBackPressed();
                                }
                            }
                        }
                    });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        grupo_id = getIntent().getIntExtra("grupo_id", -1);

        //CARREGA A INFOMAÇÃO DO GRUPO
        loading(true);
        if (!verificar_ligacao_internet()) {
            Toast.makeText(this, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
            CacheDB bd = new CacheDB(this);
            historial = bd.obter_historial(grupo_id);
            bd.close();
            mostrar_historial();
        } else {
            Ion.with(this).load(Base.API_URL + "grupohistorials/" + grupo_id).setTimeout(Base.TIMEOUT)
                    .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonObject> result) {
                    CacheDB bd = new CacheDB(EditarGrupoHistorial.this);
                    if (e != null) {
                        Toast.makeText(EditarGrupoHistorial.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        historial = bd.obter_historial(grupo_id);
                    } else {
                        if (result.getHeaders().code() == 500) {
                            Toast.makeText(EditarGrupoHistorial.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                            bd.apagar_historial(grupo_id);
                            if (result.getResult().get("code").getAsInt() == 404) {
                                onBackPressed();
                            }
                        } else if (result.getHeaders().code() != 200) {
                            Toast.makeText(EditarGrupoHistorial.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            historial = bd.obter_historial(grupo_id);
                        } else {
                            GsonBuilder gson = new GsonBuilder();
                            gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                            historial = gson.create().fromJson(result.getResult(), Historial.class);
                            bd.guardar_historial(historial);
                        }
                    }
                    bd.close();
                    mostrar_historial();
                }
            });
        }
    }

    private void mostrar_historial() {
        if (historial == null) {
            findViewById(R.id.loading_anim_editar_grupo_info).setVisibility(View.GONE);
        } else if (historial.historial != null) {
            //PARA MOSTRAR OS DADOS DO CONTEÚDO QUE VÊM EM HTML
            //VERIFICA SE O ANDROID É ANTES DO NOUGAT(7) PORQUE OS MÉTODOS DE PARSING DO HTML VARIAM
            if (Build.VERSION.SDK_INT >= 24) {
                grupo_historial.setText(Html.fromHtml(historial.historial, Html.FROM_HTML_MODE_LEGACY));
            } else {
                grupo_historial.setText(Html.fromHtml(historial.historial));
            }
            criar_novo = false;
            loading(false);
        } else {
            criar_novo = true;
            loading(false);
        }

        //CRIA OS BOTOES NA ACTION MENU CONFORME EXISTE OU NAO HISTORIAL
        invalidateOptionsMenu();
    }

    private void loading(boolean loading) {
        if (loading) {
            findViewById(R.id.loading_anim_editar_grupo_info).setVisibility(View.VISIBLE);
            findViewById(R.id.textview_grupo_info).setVisibility(View.GONE);
            findViewById(R.id.editText_grupo_info).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_anim_editar_grupo_info).setVisibility(View.GONE);
            findViewById(R.id.textview_grupo_info).setVisibility(View.VISIBLE);
            findViewById(R.id.editText_grupo_info).setVisibility(View.VISIBLE);
        }
    }

    private boolean verificar_ligacao_internet() {
        ConnectivityManager net = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return !(net.getActiveNetworkInfo() == null || !net.getActiveNetworkInfo().isConnectedOrConnecting());
    }

}
