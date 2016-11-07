package estg.saul.projetoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;

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


        ((Button) findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intente = new Intent("area_pessoal");
                intente.putExtra("grupo", grupo_selecionado);
                intente.putExtra("logado", true);
                startActivity(intente);
            }
        });
    }
}
