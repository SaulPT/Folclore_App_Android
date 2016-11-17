package estg.saul.projetoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;

/**
 * Created by SaulPT on 04/11/2016.
 */

public class Login extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.login);
        viewstub.inflate();

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GUARDA NAS DEFINIÇÕES O ESTADO DO LOGIN
                guardar_definicoes_logado(((CheckBox) findViewById(R.id.chkbox_lembrar_login)).isChecked());

                Intent intente = new Intent("area_pessoal");
                intente.putExtra("grupo_selecionado", grupo_selecionado);
                intente.putExtra("logado", true);

                intente.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(intente);
            }
        });

    }

}
