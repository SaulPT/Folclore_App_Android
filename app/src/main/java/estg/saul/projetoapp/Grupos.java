package estg.saul.projetoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.view.ViewStub;

public class Grupos extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.grupos);
        viewstub.inflate();

        View.OnClickListener cliques = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_grupo1:
                        grupo_selecionado = "Grupo 1";
                        break;
                    case R.id.btn_grupo2:
                        grupo_selecionado = "Grupo 2";
                        break;
                    case R.id.btn_grupo3:
                        grupo_selecionado = "Grupo 3";
                        break;
                }

                //GUARDA O GRUPO SELECIONADO NAS PREFERENCES SE A OPÇÃO ESTIVER ATIVA
                SharedPreferences definicoes = PreferenceManager.getDefaultSharedPreferences(Grupos.this);
                if (definicoes.getBoolean("grupo_selecionado", false)) {
                    definicoes.edit().putString("grupo_selecionado_nome", grupo_selecionado).apply();
                }

                //ACIONA O BOTAO HISTORIAL DO MENU
                onNavigationItemSelected(((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_grupo_historial));
            }
        };

        findViewById(R.id.btn_grupo1).setOnClickListener(cliques);
        findViewById(R.id.btn_grupo2).setOnClickListener(cliques);
        findViewById(R.id.btn_grupo3).setOnClickListener(cliques);

    }

}
