package estg.psi.folclore;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Contacto;

public class EditarGrupoContacto extends AppCompatActivity {

    private Contacto contacto;
    private int grupo_id;
    private EditText morada, codigopostal, website, email, telefone, telemovel, facebook;
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
        viewstub.setLayoutResource(R.layout.editar_grupo_contacto);
        viewstub.inflate();


        morada = (EditText) findViewById(R.id.editText_morada);
        codigopostal = (EditText) findViewById(R.id.editText_codigopostal);
        website = (EditText) findViewById(R.id.editText_website);
        email = (EditText) findViewById(R.id.editText_email);
        telefone = (EditText) findViewById(R.id.editText_telefone);
        telemovel = (EditText) findViewById(R.id.editText_telemovel);
        facebook = (EditText) findViewById(R.id.editText_facebook);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        if (contacto == null) {
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
                contacto = new Contacto();
                mostrar_contacto();
                break;
            case R.id.action_edit_guardar:
                //CRIA O NOVO CONTACTO
                final Contacto novo_contacto = contacto;
                novo_contacto.grupo_id = grupo_id;
                novo_contacto.morada = morada.getText().toString();
                novo_contacto.codigo_postal = codigopostal.getText().toString();
                novo_contacto.site = website.getText().toString();
                novo_contacto.email = email.getText().toString();
                novo_contacto.telefone = telefone.getText().toString();
                novo_contacto.telemovel = telemovel.getText().toString();
                novo_contacto.facebook = facebook.getText().toString();

                //SELECIONA O PEDIDO CONFORME SEJA ALTERAÇÃO OU CRIAÇÃO
                Builders.Any.B ion_load;
                if (criar_novo) {
                    ion_load = Ion.with(this).load("POST", Base.API_URL + "grupocontactos");
                } else {
                    ion_load = Ion.with(this).load("PUT", Base.API_URL + "grupocontactos/" + grupo_id);
                }

                //ENVIA O NOVO CONTACTO PARA A API
                loading(true);
                if (!verificar_ligacao_internet()) {
                    Toast.makeText(this, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
                    loading(false);
                } else {
                    ion_load.setTimeout(Base.TIMEOUT).addHeader("token", getIntent().getStringExtra("token"))
                            .setStringBody(new Gson().toJson(novo_contacto))
                            .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e != null) {
                                Toast.makeText(EditarGrupoContacto.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                                loading(false);
                            } else {
                                if (result.getHeaders().code() == 500) {
                                    Toast.makeText(EditarGrupoContacto.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                    loading(false);
                                } else if (result.getHeaders().code() != 200) {
                                    Toast.makeText(EditarGrupoContacto.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                                    loading(false);
                                } else {
                                    //EM CASO DE SUCESSO, ATUALIZA A BD E SAI DA ATIVIDADE
                                    CacheDB bd = new CacheDB(EditarGrupoContacto.this);
                                    bd.guardar_contacto(novo_contacto);
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
                    Ion.with(this).load("DELETE", Base.API_URL + "grupocontactos/" + grupo_id).setTimeout(Base.TIMEOUT)
                            .addHeader("token", getIntent().getStringExtra("token"))
                            .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e != null) {
                                Toast.makeText(EditarGrupoContacto.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                                loading(false);
                            } else {
                                if (result.getHeaders().code() == 500) {
                                    Toast.makeText(EditarGrupoContacto.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                    loading(false);
                                } else if (result.getHeaders().code() != 200) {
                                    Toast.makeText(EditarGrupoContacto.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                                    loading(false);
                                } else {
                                    //EM CASO DE SUCESSO, ATUALIZA A BD E SAI DA ATIVIDADE
                                    CacheDB bd = new CacheDB(EditarGrupoContacto.this);
                                    bd.apagar_contacto(grupo_id);
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
            contacto = bd.obter_contacto(grupo_id);
            bd.close();
            mostrar_contacto();
        } else {
            Ion.with(this).load(Base.API_URL + "grupocontactos/" + grupo_id).setTimeout(Base.TIMEOUT)
                    .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonObject> result) {
                    CacheDB bd = new CacheDB(EditarGrupoContacto.this);
                    if (e != null) {
                        Toast.makeText(EditarGrupoContacto.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        contacto = bd.obter_contacto(grupo_id);
                    } else {
                        if (result.getHeaders().code() == 500) {
                            Toast.makeText(EditarGrupoContacto.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                            bd.apagar_contacto(grupo_id);
                            if (result.getResult().get("code").getAsInt() == 404) {
                                onBackPressed();
                            }
                        } else if (result.getHeaders().code() != 200) {
                            Toast.makeText(EditarGrupoContacto.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            contacto = bd.obter_contacto(grupo_id);
                        } else {
                            contacto = new Gson().fromJson(result.getResult(), Contacto.class);
                            bd.guardar_contacto(contacto);
                        }
                    }
                    bd.close();
                    mostrar_contacto();
                }
            });
        }
    }

    private void mostrar_contacto() {
        if (contacto == null) {
            findViewById(R.id.loading_anim_editar_grupo_contacto).setVisibility(View.GONE);
        } else if (contacto.grupo_id != 0) {
            morada.setText(contacto.morada);
            codigopostal.setText(contacto.codigo_postal);
            website.setText(contacto.site);
            email.setText(contacto.email);
            telefone.setText(contacto.telefone);
            telemovel.setText(contacto.telemovel);
            facebook.setText(contacto.facebook);
            criar_novo = false;
            loading(false);
        } else {
            criar_novo = true;
            loading(false);
        }

        //CRIA OS BOTOES NA ACTION MENU CONFORME EXISTE OU NÃO GRUPOS GERENTES
        invalidateOptionsMenu();
    }

    private void loading(boolean loading) {
        if (loading) {
            findViewById(R.id.loading_anim_editar_grupo_contacto).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_editar_grupo_contactos).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_anim_editar_grupo_contacto).setVisibility(View.GONE);
            findViewById(R.id.layout_editar_grupo_contactos).setVisibility(View.VISIBLE);
        }
    }

    private boolean verificar_ligacao_internet() {
        ConnectivityManager net = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return !(net.getActiveNetworkInfo() == null || !net.getActiveNetworkInfo().isConnectedOrConnecting());
    }

}
