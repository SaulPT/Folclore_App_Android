package estg.saul.projetoapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

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

                String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
                String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

                //ENVIAR GET PARA URL DA API
                Ion.with(getApplicationContext())
                        .load("GET", "http://10.0.2.2:80/user/login")
                        .setTimeout(10000)
                        .addHeader("username", username)
                        .addHeader("password", password)
                        .addHeader("dispositivo", "Android")
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                //EM CASO DE ERRO
                                if (e != null) {
                                    Toast.makeText(Login.this, "ERRO: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                //EM CASO DE SUCESSO
                                if (result != null) {
                                    Toast.makeText(Login.this, result, Toast.LENGTH_SHORT).show();

                                    /*
                                    //GUARDA NAS DEFINIÇÕES O ESTADO DO LOGIN E O TOKEN
                                    guardar_definicoes_logado(((CheckBox) findViewById(R.id.chkbox_lembrar_login)).isChecked());
                                    PreferenceManager.getDefaultSharedPreferences(this).edit()
                                            .putString("token",result.get("token").getAsString()).apply();


                                    Intent intente = new Intent("area_pessoal");
                                    intente.putExtra("grupo_selecionado", grupo_selecionado);
                                    intente.putExtra("logado", true);
                                    intente.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(intente);
                                    */
                                }
                            }
                        });

            }
        });

    }

}
