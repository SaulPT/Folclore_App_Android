package estg.saul.projetoapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.List;

import estg.saul.projetoapp.database.CacheDB;
import estg.saul.projetoapp.model.Noticia;

public class HomeNoticias extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.nav_noticias);


        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.noticias);
        viewstub.inflate();


        //OBTEM O LOGIN E O GRUPO SELECIONADO PELAS DEFINICOES GRAVADAS
        SharedPreferences definicoes = PreferenceManager.getDefaultSharedPreferences(this);
        logado = definicoes.getBoolean("logado", false);
        username = definicoes.getString("username", null);
        token = definicoes.getString("token", null);
        grupo_selecionado = definicoes.getString("grupo_selecionado_nome", null);


        //APENAS PARA NA FUNÇÃO "checkar_estado_grupo_login" SABER SE DEVE CARREGAR O
        //GRUPO SELECIONADO PELAS PREFERENCES (1º ARRANQUE) OU PELA VARIÁVEL
        definicoes.edit().putBoolean("grupo_auto", true).apply();


        //TESTE PARA MOSTRAR BD
        BD = new CacheDB(this);
        Cursor cursor = BD.obter_noticias();
        Toast.makeText(HomeNoticias.this, "BD tem " + cursor.getCount() + " noticias", Toast.LENGTH_SHORT).show();


        //OBTEM AS NOTICIAS ATRAVES DA API
        Ion.with(getApplicationContext())
                .load("GET", "http://10.0.2.2:80/Folclore_API/api/noticias")
                .setTimeout(10000)
                .asJsonArray()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonArray>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonArray> result) {
                        //EM CASO DE ERRO NA LIGAÇÃO
                        if (e != null) {
                            Toast.makeText(HomeNoticias.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        } else {

                            //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                            if (result.getHeaders().code() != 200) {
                                //SE A API DESOLVEU ERRO
                                Toast.makeText(HomeNoticias.this, result.getHeaders().message(), Toast.LENGTH_SHORT).show();
                            } else {
                                //SE A API DEVOLVEU COM SUCESSO AS NOTICIAS
                                List<Noticia> noticias;
                                noticias = new Gson().fromJson(result.getResult(), new TypeToken<List<Noticia>>() {
                                }.getType());
                                BD.inserir_noticias(noticias);
                            }
                        }


                        //ESCONDE O LOADING QUANDO RECEBE OS DADOS
                        LinearLayout loading_layout = (LinearLayout) findViewById(R.id.loading_loading);
                        ViewGroup.LayoutParams params = loading_layout.getLayoutParams();
                        params.height = 0;
                        loading_layout.setLayoutParams(params);
                    }
                });

    }


    @Override
    public void onDestroy() {
        //APAGA O GRUPO SELECIONADO NAS PREFERENCES SE A OPÇÃO ESTIVER DESATIVADA
        SharedPreferences definicoes = PreferenceManager.getDefaultSharedPreferences(this);
        if (!definicoes.getBoolean("grupo_selecionado", false)) {
            definicoes.edit().remove("grupo_selecionado_nome").apply();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }

}
