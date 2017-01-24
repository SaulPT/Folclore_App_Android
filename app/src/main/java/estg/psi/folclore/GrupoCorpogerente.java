package estg.psi.folclore;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Corpogerente;

public class GrupoCorpogerente extends Base {

    private TextView textview_corpogerente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.grupo_info);
        viewstub.inflate();

        textview_corpogerente = (TextView) findViewById(R.id.textview_conteudo_info);
        ((TextView) findViewById(R.id.textview_titulo_info)).setText(R.string.staff);
    }

    @Override
    public void onResume() {
        super.onResume();

        final CacheDB bd = new CacheDB(this);

        if (grupo_selecionado == -1) {
            onBackPressed();
        } else {
            //VERIFICA SE HÁ LIGAÇÃO À INTERNET
            loading(true);
            if (!verificar_ligacao_internet()) {
                this.setTitle(bd.obter_grupo(grupo_selecionado).abreviatura);
                mostrar_corpogerente(bd.obter_corpogerente(grupo_selecionado));
                bd.close();
            } else {
                //SE SIM, ACEDE À API
                Ion.with(this).load(API_URL + "grupocorpogerentes/" + grupo_selecionado).setTimeout(TIMEOUT).asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        //EM CASO DE ERRO NA LIGAÇÃO
                        if (e != null) {
                            Toast.makeText(GrupoCorpogerente.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                            if (result.getHeaders().code() == 500) {
                                //SE A API DEVOLVEU UM ERRO CONHECIDO
                                Toast.makeText(GrupoCorpogerente.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                bd.apagar_corpogerente(grupo_selecionado);
                                if (result.getResult().get("code").getAsInt() == 404) {
                                    grupo_selecionado = -1;
                                    onBackPressed();
                                }
                            } else if (result.getHeaders().code() != 200) {
                                //SE A API DEVOLVEU UM ERRO DESCONHECIDO
                                Toast.makeText(GrupoCorpogerente.this, "Erro do servidor (" + result.getHeaders().code() + " - " + result.getHeaders().message() + ")", Toast.LENGTH_SHORT).show();
                            } else {
                                //SE A API DEVOLVEU OS DADOS COM SUCESSO, DESERIALIZA E ATUALIZA A BD
                                GsonBuilder gson = new GsonBuilder();
                                gson.setDateFormat(CacheDB.DATE_TIME_FORMAT);
                                Corpogerente corpogerente = gson.create().fromJson(result.getResult(), Corpogerente.class);
                                bd.guardar_corpogerente(corpogerente);
                            }
                        }

                        if (grupo_selecionado != -1) {
                            GrupoCorpogerente.this.setTitle(bd.obter_grupo(grupo_selecionado).abreviatura);
                            mostrar_corpogerente(bd.obter_corpogerente(grupo_selecionado));
                        } else {
                            onBackPressed();
                        }
                        bd.close();
                    }
                });
            }
        }

    }

    private void mostrar_corpogerente(Corpogerente corpogerente) {
        if (corpogerente == null) {
            textview_corpogerente.setText(null);
            Toast.makeText(this, "O grupo não tem corpos gerentes", Toast.LENGTH_SHORT).show();
        } else {
            //PARA MOSTRAR OS DADOS DO CONTEÚDO QUE VÊM EM HTML
            //VERIFICA SE O ANDROID É ANTES DO NOUGAT(7) PORQUE OS MÉTODOS DE PARSING DO HTML VARIAM
            if (Build.VERSION.SDK_INT >= 24) {
                textview_corpogerente.setText(Html.fromHtml(corpogerente.corposgerentes, Html.FROM_HTML_MODE_LEGACY));
            } else {
                textview_corpogerente.setText(Html.fromHtml(corpogerente.corposgerentes));
            }
        }

        loading(false);
    }

    private void loading(boolean loading) {
        if (loading) {
            findViewById(R.id.loading_anim_grupo_info).setVisibility(View.VISIBLE);
            findViewById(R.id.textview_titulo_info).setVisibility(View.GONE);
            findViewById(R.id.textview_conteudo_info).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_anim_grupo_info).setVisibility(View.GONE);
            findViewById(R.id.textview_titulo_info).setVisibility(View.VISIBLE);
            findViewById(R.id.textview_conteudo_info).setVisibility(View.VISIBLE);
        }
    }

}
