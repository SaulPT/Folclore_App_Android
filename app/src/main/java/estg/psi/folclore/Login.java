package estg.psi.folclore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;


public class Login extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.login);
        viewstub.inflate();


        final ProgressBar loading = ((ProgressBar) findViewById(R.id.progressloading));
        final Button botao_login = ((Button) findViewById(R.id.btn_login));
        botao_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                botao_login.setEnabled(false);
                loading.setVisibility(View.VISIBLE);

                final String txt_username = ((EditText) findViewById(R.id.login_username)).getText().toString();
                String password = ((EditText) findViewById(R.id.login_password)).getText().toString();


                //ENVIAR POST PARA URL DA API
                Ion.with(Login.this)
                        .load("POST", API_URL + "/user/login")
                        .setTimeout(10000)
                        .addHeader("username", txt_username)
                        .addHeader("password", password)
                        .addHeader("dispositivo", "Android")
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                //EM CASO DE ERRO NA LIGAÇÃO
                                if (e != null) {
                                    Toast.makeText(Login.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                                } else {
                                    //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                                    if (result.getHeaders().code() != 200) {
                                        Toast.makeText(Login.this, "Erro de autenticação", Toast.LENGTH_SHORT).show();
                                    } else {
                                        logado = true;
                                        username = txt_username;
                                        token = result.getResult().get("token").getAsString();

                                        //GUARDA NAS DEFINIÇÕES O ESTADO DO LOGIN E O TOKEN
                                        guardar_definicoes_logado(((CheckBox) findViewById(R.id.chkbox_lembrar_login)).isChecked());

                                        iniciar_intente_extras(new Intent("estg.psi.folclore.AREAPESSOAL"));
                                    }
                                }

                                botao_login.setEnabled(true);
                                loading.setVisibility(View.GONE);
                            }
                        });

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        setResult(1, data);

        //TERMINA A ATIVIDADE PARA NAO SER ACEDIDA COM O BOTAO "VOLTAR"
        finish();
    }


}
