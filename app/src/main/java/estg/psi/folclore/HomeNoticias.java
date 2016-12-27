package estg.psi.folclore;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
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

import estg.psi.folclore.adapter.NoticiasAdapter;
import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Noticia;

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
    }


    @Override
    public void onResume() {
        super.onResume();

        findViewById(R.id.loading_noticias).setVisibility(View.VISIBLE);

        final CacheDB bd = new CacheDB(this);

        //VERIFICA SE O TELEMÓVEL ESTÁ LIGADO À INTERNET
        ConnectivityManager net = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (net.getActiveNetworkInfo() == null || !net.getActiveNetworkInfo().isConnectedOrConnecting()) {
            Toast.makeText(this, "Sem acesso à internet. A mostrar dados locais", Toast.LENGTH_SHORT).show();
            findViewById(R.id.loading_noticias).setVisibility(View.GONE);

            //MOSTRAR DA BD
            NoticiasAdapter noticias_adapter = new NoticiasAdapter(this, R.id.listview_noticias, bd.obter_noticias());
            ((ListView) findViewById(R.id.listview_noticias)).setAdapter(noticias_adapter);
        } else {
            //OBTEM AS NOTICIAS ATRAVES DA API
            Ion.with(this)
                    .load("GET", API_URL + "/noticias")
                    .setTimeout(10000)
                    .asJsonArray()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonArray>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonArray> result) {
                            //EM CASO DE ERRO NA LIGAÇÃO
                            if (e != null) {
                                Toast.makeText(HomeNoticias.this, "Erro na ligação ao servidor. A mostrar dados locais", Toast.LENGTH_SHORT).show();
                                //MOSTRAR DA BD
                                NoticiasAdapter noticias_adapter = new NoticiasAdapter(HomeNoticias.this, R.id.listview_noticias, bd.obter_noticias());
                                ((ListView) findViewById(R.id.listview_noticias)).setAdapter(noticias_adapter);

                            } else {
                                //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                                if (result.getHeaders().code() != 200) {
                                    //SE A API DESOLVEU ERRO
                                    Toast.makeText(HomeNoticias.this, result.getHeaders().message() + ". A mostrar dados locais", Toast.LENGTH_SHORT).show();
                                    //MOSTRAR DA BD
                                    NoticiasAdapter noticias_adapter = new NoticiasAdapter(HomeNoticias.this, R.id.listview_noticias, bd.obter_noticias());
                                    ((ListView) findViewById(R.id.listview_noticias)).setAdapter(noticias_adapter);

                                } else {
                                    //SE A API DEVOLVEU COM SUCESSO AS NOTICIAS
                                    GsonBuilder gson = new GsonBuilder();
                                    gson.setDateFormat("yyyy-MM-dd HH:mm:ss");
                                    List<Noticia> noticias = gson.create().fromJson(result.getResult(), new TypeToken<List<Noticia>>() {
                                    }.getType());

                                    //ACTUALIZA A BD COM OS DADOS RECEBIDOS
                                    bd.apagar_noticias();
                                    bd.inserir_noticias(noticias);

                                    //MOSTRAR DA API
                                    NoticiasAdapter noticias_adapter = new NoticiasAdapter(HomeNoticias.this, R.id.listview_noticias, bd.obter_noticias());
                                    ((ListView) findViewById(R.id.listview_noticias)).setAdapter(noticias_adapter);
                                }
                            }


                            findViewById(R.id.loading_noticias).setVisibility(View.GONE);
                        }
                    });
        }
        bd.close();
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