package estg.saul.projetoapp;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;

/**
 * Created by SaulPT on 01/11/2016.
 */

public class Grupos extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.grupos);
        viewstub.inflate();

        checkar_estado_navigation_view();


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

                //ACIONA O BOTAO HISTORIAL DO MENU
                onNavigationItemSelected(((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_grupo_historial));
            }
        };

        ((Button) findViewById(R.id.btn_grupo1)).setOnClickListener(cliques);
        ((Button) findViewById(R.id.btn_grupo2)).setOnClickListener(cliques);
        ((Button) findViewById(R.id.btn_grupo3)).setOnClickListener(cliques);
    }

}
