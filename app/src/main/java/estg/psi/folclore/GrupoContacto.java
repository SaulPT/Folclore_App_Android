package estg.psi.folclore;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Contacto;

public class GrupoContacto extends Base {

    private TextView textview_morada, textView_codigopstal, textView_website, textView_email, textView_telefone, textView_telemovel, textView_facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.grupo_contacto);
        viewstub.inflate();

        textview_morada = (TextView) findViewById(R.id.textview_morada);
        textView_codigopstal = (TextView) findViewById(R.id.textview_codigopostal);
        textView_website = (TextView) findViewById(R.id.textview_website);
        textView_email = (TextView) findViewById(R.id.textview_email);
        textView_telefone = (TextView) findViewById(R.id.textview_telefone);
        textView_telemovel = (TextView) findViewById(R.id.textview_telemovel);
        textView_facebook = (TextView) findViewById(R.id.textview_facebook);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (grupo_selecionado == -1) {
            onBackPressed();
        } else {
            final CacheDB bd = new CacheDB(this);
            loading(true);
            //VERIFICA SE HÁ LIGAÇÃO À INTERNET
            if (!verificar_ligacao_internet()) {
                this.setTitle(bd.obter_grupo(grupo_selecionado).abreviatura);
                mostrar_contacto(bd.obter_contacto(grupo_selecionado));
                bd.close();
            } else {
                //SE SIM, ACEDE À API
                Ion.with(this).load(API_URL + "grupocontactos/" + grupo_selecionado).setTimeout(TIMEOUT).asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        //EM CASO DE ERRO NA LIGAÇÃO
                        if (e != null) {
                            Toast.makeText(GrupoContacto.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                            if (result.getHeaders().code() == 500) {
                                //SE A API DEVOLVEU UM ERRO CONHECIDO
                                Toast.makeText(GrupoContacto.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                bd.apagar_contacto(grupo_selecionado);
                                if (result.getResult().get("code").getAsInt() == 404) {
                                    grupo_selecionado = -1;
                                    onBackPressed();
                                }
                            } else if (result.getHeaders().code() != 200) {
                                //SE A API DEVOLVEU UM ERRO DESCONHECIDO
                                Toast.makeText(GrupoContacto.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            } else {
                                //SE A API DEVOLVEU OS DADOS COM SUCESSO, DESERIALIZA E ATUALIZA A BD
                                Contacto contacto = new Gson().fromJson(result.getResult(), Contacto.class);
                                bd.guardar_contacto(contacto);
                            }
                        }

                        if (grupo_selecionado != -1) {
                            GrupoContacto.this.setTitle(bd.obter_grupo(grupo_selecionado).abreviatura);
                            mostrar_contacto(bd.obter_contacto(grupo_selecionado));
                        } else {
                            onBackPressed();
                        }
                        bd.close();
                    }
                });
            }
        }

    }

    private void mostrar_contacto(Contacto contacto) {
        if (contacto == null) {
            textview_morada.setText(null);
            textView_codigopstal.setText(null);
            textView_website.setText(null);
            textView_email.setText(null);
            textView_telefone.setText(null);
            textView_telemovel.setText(null);
            textView_facebook.setText(null);
            Toast.makeText(this, "O grupo não tem contactos", Toast.LENGTH_SHORT).show();
        } else {
            textview_morada.setText(contacto.morada);
            textView_codigopstal.setText(contacto.codigo_postal);
            textView_website.setText(contacto.site);
            textView_email.setText(contacto.email);
            textView_telefone.setText(contacto.telefone);
            textView_telemovel.setText(contacto.telemovel);
            textView_facebook.setText(contacto.facebook);
        }

        loading(false);
    }

    private void loading(boolean loading) {
        if (loading) {
            findViewById(R.id.loading_anim_grupo_contacto).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_grupo_contactos).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_anim_grupo_contacto).setVisibility(View.GONE);
            findViewById(R.id.layout_grupo_contactos).setVisibility(View.VISIBLE);
        }
    }

}
