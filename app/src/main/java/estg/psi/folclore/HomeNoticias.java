package estg.psi.folclore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.nav_news);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.listview_dados_api);
        viewstub.inflate();

        //OBTEM O LOGIN E O GRUPO SELECIONADO PELAS DEFINICOES GRAVADAS
        SharedPreferences definicoes = PreferenceManager.getDefaultSharedPreferences(this);
        logado = definicoes.getBoolean("logado", false);
        username = definicoes.getString("username", null);
        token = definicoes.getString("token", null);
        grupo_selecionado = definicoes.getInt("grupo_selecionado", -1);

        //APENAS PARA NA FUNÇÃO "checkar_estado_grupo_login" SABER SE DEVE CARREGAR O
        //GRUPO SELECIONADO PELAS PREFERENCES (1º ARRANQUE) OU PELA VARIÁVEL
        if (getIntent().getAction().equals("android.intent.action.MAIN")) {
            definicoes.edit().putBoolean("grupo_auto", true).apply();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        loading(true);
        final CacheDB bd = new CacheDB(this);

        //VERIFICA SE HÁ LIGAÇÃO À INTERNET
        if (!verificar_ligacao_internet()) {
            mostrar_dados(bd.obter_noticias());
            bd.close();
            loading(false);
        } else {
            //SE SIM, ACEDE À API
            Ion.with(this).load(API_URL + "noticias").setTimeout(TIMEOUT).asJsonArray().withResponse().setCallback(new FutureCallback<Response<JsonArray>>() {
                @Override
                public void onCompleted(Exception e, Response<JsonArray> result) {
                    //EM CASO DE ERRO NA LIGAÇÃO
                    if (e != null) {
                        Toast.makeText(HomeNoticias.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        mostrar_dados(bd.obter_noticias());
                    } else {
                        //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                        if (result.getHeaders().code() != 200) {
                            //SE A API DESOLVEU ERRO
                            Toast.makeText(HomeNoticias.this, "Erro do servidor (" + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            mostrar_dados(bd.obter_noticias());
                        } else {
                            //SE A API DEVOLVEU OS DADOS COM SUCESSO, DESERIALIZA E ATUALIZA A BD
                            GsonBuilder gson = new GsonBuilder();
                            gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                            List<Noticia> noticias = gson.create().fromJson(result.getResult(), new TypeToken<List<Noticia>>() {
                            }.getType());
                            bd.guardar_noticias(noticias);
                            mostrar_dados(noticias);
                        }
                    }
                    bd.close();
                    loading(false);
                }
            });
        }
    }

    private void mostrar_dados(List<Noticia> noticias) {
        NoticiasAdapter noticias_adapter = new NoticiasAdapter(this, R.id.listview_dados_api, Noticia.ordenar_noticias_data_desc(noticias));
        ((ListView) findViewById(R.id.listview_dados_api)).setAdapter(noticias_adapter);
    }


    //PARA TERMINAR A APP SEMPRE QUE 'RETROCEDEMOS' NO ECRA NOTICIAS
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sair_app_ecra_noticias", true)) {
                finishAffinity();
            } else {
                super.onBackPressed();
            }
        }
    }

}
