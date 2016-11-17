package estg.saul.projetoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewStub;

public class HomeNoticias extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.noticias);
        viewstub.inflate();


        //OBTEM O LOGIN E O GRUPO SELECIONADO PELAS DEFINICOES GRAVADAS
        logado = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("logado", false);
        grupo_selecionado = PreferenceManager.getDefaultSharedPreferences(this).getString("grupo_selecionado_nome", null);


        //APENAS PARA NA FUNÇÃO "checkar_estado_grupo_login" SABER SE DEVE CARREGAR O
        //GRUPO SELECIONADO PELAS PREFERENCES (1º ARRANQUE) OU PELA VARIÁVEL
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("grupo_auto", true).apply();
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

}
