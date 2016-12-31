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

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Concelho;
import estg.psi.folclore.model.Grupo;

public class EditarGrupoDetalhes extends AppCompatActivity {

    String[] distritos;
    Concelho[] concelhos;
    Grupo grupo;
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


        loading(true);
        if (!verificar_ligacao_internet()) {
            Toast.makeText(this, "Não foi possível carregar distritos e concelhos", Toast.LENGTH_SHORT).show();
        } else {
            //CARREGA OS DISTRITOS
            Ion.with(this).load(Base.API_URL + "distritos").setTimeout(Base.TIMEOUT).asJsonArray().withResponse().setCallback(new FutureCallback<Response<JsonArray>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonArray> result) {
                    if (e != null) {
                        Toast.makeText(EditarGrupoDetalhes.this, "Não foi possível carregar distritos e concelhos", Toast.LENGTH_SHORT).show();
                    } else {
                        if (result.getHeaders().code() != 200) {
                            Toast.makeText(EditarGrupoDetalhes.this, "Não foi possível carregar distritos e concelhos", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditarGrupoDetalhes.this, "Não foi possível carregar distritos e concelhos", Toast.LENGTH_SHORT).show();
                } else {
                    if (result.getHeaders().code() != 200) {
                        Toast.makeText(EditarGrupoDetalhes.this, "Não foi possível carregar distritos e concelhos", Toast.LENGTH_SHORT).show();
                    } else {
                        concelhos = new Gson().fromJson(result.getResult(), Concelho[].class);
                        carregar_grupo();
                    }
                }
            }
        });
    }

    private void carregar_grupo() {
        final int grupo_id = getIntent().getIntExtra("grupo_id", -1);

        //CARREGA AS INFORMAÇÕES
        Ion.with(this).load(Base.API_URL + "grupos/" + grupo_id).setTimeout(Base.TIMEOUT).asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                CacheDB bd = new CacheDB(EditarGrupoDetalhes.this);
                if (e != null) {
                    Toast.makeText(EditarGrupoDetalhes.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                    mostrar_grupo(bd.obter_grupo(grupo_id));
                } else {
                    if (result.getHeaders().code() != 200) {
                        Toast.makeText(EditarGrupoDetalhes.this, "Erro do servidor (" + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                        mostrar_grupo(bd.obter_grupo(grupo_id));
                    } else {
                        GsonBuilder gson = new GsonBuilder();
                        gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                        grupo = gson.create().fromJson(result.getResult(), Grupo.class);
                        bd.guardar_grupo(grupo);

                        //USA O ADAPTER DOS GRUPOS PARA MOSTRAR OS DADOS DO GRUPO OBTIDO
                        mostrar_grupo(grupo);
                    }
                }
                bd.close();
                loading(false);
            }
        });

        //CARREGA A IMAGEM
        final ImageView imageview = (ImageView) findViewById(R.id.grupo_imagem);
        Ion.with(this).load(Base.IMG_URL + "grupos/" + grupo_id).setTimeout(Base.TIMEOUT).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (e != null) {
                    imageview.setImageResource(R.drawable.default_noticias);
                } else {
                    imageview.setImageBitmap(result);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        menu.findItem(R.id.edit_eliminar).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_confirmar:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loading(boolean loading) {
        if (loading) {
            findViewById(R.id.loading_anim_grupo_detalhes).setVisibility(View.VISIBLE);
            findViewById(R.id.grupo_imagem).setVisibility(View.GONE);
            findViewById(R.id.textview_grupo_abreviatura).setVisibility(View.GONE);
            grupo_abreviatura.setVisibility(View.GONE);
            findViewById(R.id.textview_distrito).setVisibility(View.GONE);
            grupo_distrito.setVisibility(View.GONE);
            findViewById(R.id.textview_concelho).setVisibility(View.GONE);
            grupo_concelho.setVisibility(View.GONE);
            findViewById(R.id.textview_grupo_nome).setVisibility(View.GONE);
            grupo_nome.setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_anim_grupo_detalhes).setVisibility(View.GONE);
            findViewById(R.id.grupo_imagem).setVisibility(View.VISIBLE);
            findViewById(R.id.textview_grupo_abreviatura).setVisibility(View.VISIBLE);
            grupo_abreviatura.setVisibility(View.VISIBLE);
            findViewById(R.id.textview_distrito).setVisibility(View.VISIBLE);
            grupo_distrito.setVisibility(View.VISIBLE);
            findViewById(R.id.textview_concelho).setVisibility(View.VISIBLE);
            grupo_concelho.setVisibility(View.VISIBLE);
            findViewById(R.id.textview_grupo_nome).setVisibility(View.VISIBLE);
            grupo_nome.setVisibility(View.VISIBLE);
        }
    }

    private boolean verificar_ligacao_internet() {
        ConnectivityManager net = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (net.getActiveNetworkInfo() != null || net.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    private void mostrar_grupo(Grupo grupo) {
        grupo_abreviatura.setText(grupo.abreviatura);
        grupo_nome.setText(grupo.nome);
        grupo_distrito.setAdapter(new ArrayAdapter<String>(this, R.layout.item_spinner, distritos));

        //PARA SABER O DISTRITO DO GRUPO
        int x = 0;
        int grupo_distrito_id = -1;
        do {
            if (concelhos[x].id == grupo.concelho_id) {
                grupo_distrito_id = concelhos[x].distrito_id;
            }
            x++;
        } while (grupo_distrito_id == -1);

        grupo_distrito.setSelection(grupo_distrito_id);
    }

}
