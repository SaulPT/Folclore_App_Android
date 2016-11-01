package estg.saul.projetoapp;

import android.os.Bundle;
import android.view.ViewStub;
import android.widget.TextView;

/**
 * Created by SaulPT on 01/11/2016.
 */

public class GrupoInfo extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.grupo_info);
        viewstub.inflate();

        //RECEBE A INFO DA ATIVIDADE ANTERIOR PELO INTENT
        ((TextView) findViewById(R.id.txt_info)).setText(getIntent().getStringExtra("grupo"));

        //VOLTA A COLOCAR ESSA INFO NA VARIAVEL GLOBAL (há outra forma)?????
        grupo_selecionado = (getIntent()).getStringExtra("grupo");


        checkar_estado_navigation_view();
    }
}
