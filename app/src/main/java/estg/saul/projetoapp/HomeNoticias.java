package estg.saul.projetoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewStub;

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
