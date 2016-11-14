package estg.saul.projetoapp;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewStub;

public class HomeNoticias extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //OBTEM O ESTADO DO LOGIN NO ARRANQUE DA APLICAÇAO ATRAVÉS DAS PREFERENCES
        logado = getSharedPreferences("definicoes", MODE_PRIVATE).getBoolean("logado", false);


        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.noticias);
        viewstub.inflate();


        //OBTEM O LOGIN E O GRUPO SELECIONADO PELAS DEFINICOES GRAVADAS
        logado = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("logado", false);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("grupo_selecionado", false)) {
            grupo_selecionado = "Grupo 3";
        } else {
            grupo_selecionado = null;
        }
    }

}
