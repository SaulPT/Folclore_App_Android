package estg.psi.folclore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import estg.psi.folclore.database.CacheDB;

public class Base extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String IMG_URL = "http://10.0.2.2/FolcloreOnline/backend/web/upload/";
    public static final String API_URL = "http://10.0.2.2/FolcloreOnline/api/";
    public static final int TIMEOUT = 10000;
    //protected static final String API_URL = "http://www.folcloreonline.pt/api";
    //public static final String IMG_URL = "http://www.folcloreonline.pt/admin/upload/";
    protected int grupo_selecionado;
    protected String username, token;
    protected boolean logado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CARREGA O LAYOUT PRINCIPAL
        setContentView(R.layout.home);
        //

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences definicoes = PreferenceManager.getDefaultSharedPreferences(this);

        if (getIntent() != null) {
            //DEFINE O ESTADO DOS ITEMS DO MENU DO GRUPO COM BASE NA VARIAVEL GLOBAL
            if (getClass().getSimpleName().equals("HomeNoticias") && definicoes.getBoolean("grupo_auto", false)) {
                definicoes.edit().remove("grupo_auto").apply();
            } else {
                grupo_selecionado = getIntent().getIntExtra("grupo_selecionado", -1);
            }

            Menu m = ((NavigationView) findViewById(R.id.nav_view)).getMenu();
            if (grupo_selecionado == -1) {
                m.setGroupEnabled(R.id.nav_menu_grupo, false);
            } else {
                m.setGroupEnabled(R.id.nav_menu_grupo, true);
            }

            //VERIFICA O ESTADO DO LOGIN
            if (!logado) {
                logado = getIntent().getBooleanExtra("logado", false);
                username = getIntent().getStringExtra("username");
                token = getIntent().getStringExtra("token");
            }


            atualizar_nav_header_action_menu();
        }

        //GUARDA O GRUPO SELECIONADO NAS SHAREDPREFERENCES SE A OPÇÃO ESTIVER ATIVA
        if (definicoes.getBoolean("guardar_grupo_selecionado", false)) {
            definicoes.edit().putInt("grupo_selecionado", grupo_selecionado).apply();
        } else {
            definicoes.edit().remove("grupo_selecionado").apply();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            onNewIntent(data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            setResult(1, getIntent());
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);


        //VERIFICA SE EXISTE ALGUM USER LOGADO PARA MOSTRAR OU NAO A OPÇAO "LOGOUT"
        if (logado) {
            menu.findItem(R.id.action_logout).setVisible(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        switch (item.getItemId()) {
            case R.id.action_definicoes:
                iniciar_intente_extras(new Intent("estg.psi.folclore.DEFINICOES"));
                break;
            case R.id.action_area_pessoal:
                if (verificar_ligacao_internet()) {
                    if (logado) {
                        iniciar_intente_extras(new Intent("estg.psi.folclore.AREAPESSOAL"));
                    } else {
                        iniciar_intente_extras(new Intent("estg.psi.folclore.LOGIN"));
                    }
                }
                break;
            case R.id.action_logout:
                final String nome_classe_actual = getClass().getSimpleName();
                if (verificar_ligacao_internet()) {
                    Ion.with(this).load("POST", API_URL + "user/logout").setTimeout(TIMEOUT)
                            .addHeader("token", token)
                            .asJsonObject().withResponse()
                            .setCallback(new FutureCallback<Response<JsonObject>>() {
                                @Override
                                public void onCompleted(Exception e, Response<JsonObject> result) {
                                    //EM CASO DE ERRO NA LIGAÇÃO
                                    if (e != null) {
                                        Toast.makeText(Base.this, "Erro na ligação ao servidor", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //EM CASO DE SUCESSO NA LIGAÇÃO VERIFICA O TIPO DE RESULTADO OBTIDO
                                        if (result.getHeaders().code() != 200) {
                                            Toast.makeText(Base.this, "Erro de autenticação", Toast.LENGTH_SHORT).show();
                                        } else {
                                            logado = false;
                                            username = null;
                                            token = null;

                                            //GUARDA NAS DEFINIÇÕES O ESTADO DO LOGIN E O TOKEN
                                            guardar_definicoes_logado(logado);

                                            //SE O ECRÃ ATUAL FOR PRIVADO, CARREGA UMA NOVA ATIVIDADE
                                            if (nome_classe_actual.equals("AreaPessoal")) {
                                                iniciar_intente_extras(new Intent("estg.psi.folclore.LOGIN"));
                                            } else {
                                                atualizar_nav_header_action_menu();
                                            }
                                        }
                                    }
                                }
                            });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        Intent intente;

        switch (item.getItemId()) {
            case R.id.nav_noticias:
                intente = new Intent("estg.psi.folclore.NOTICIAS");
                break;
            case R.id.nav_eventos:
                intente = new Intent("estg.psi.folclore.EVENTOS");
                break;
            case R.id.nav_parcerias:
                intente = new Intent("estg.psi.folclore.PARCERIAS");
                break;
            case R.id.nav_grupos:
                intente = new Intent("estg.psi.folclore.GRUPOS");
                break;
            default:
                intente = new Intent("estg.psi.folclore.GRUPODETALHES");
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        iniciar_intente_extras(intente);

        return true;
    }


    //////////////////////////
    //FUNÇÕES PERSONALIZADAS//
    //////////////////////////

    protected void atualizar_nav_header_action_menu() {
        //MOSTRA O NOME DO UTILIZADOR
        View navview = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        TextView txtusername = (TextView) navview.findViewById(R.id.txt_username);
        if (logado) {
            txtusername.setText(username);
        } else {
            txtusername.setText(R.string.nav_public_username);
        }

        //MOSTRA O NOME DO GRUPO SELECIONADO
        NavigationView nav_view = ((NavigationView) findViewById(R.id.nav_view));
        CacheDB bd = new CacheDB(this);
        if (grupo_selecionado != -1 && bd.obter_grupo(grupo_selecionado) != null) {
            nav_view.getMenu().findItem(R.id.nav_grupo_selecionado).setTitle(bd.obter_grupo(grupo_selecionado).abreviatura.toUpperCase());
        } else {
            nav_view.getMenu().findItem(R.id.nav_grupo_selecionado).setTitle("NENHUM GRUPO SELECIONADO");
        }
        bd.close();

        //PARA VOLTAR A CHAMAR A FUNÇÃO QUE CRIA O MENU (onCreateOptionsMenu)
        invalidateOptionsMenu();
    }


    protected void guardar_definicoes_logado(boolean lembrar_login) {
        SharedPreferences.Editor definicoes = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (lembrar_login) {
            definicoes.putBoolean("logado", lembrar_login);
            definicoes.putString("username", username);
            definicoes.putString("token", token);
        } else {
            definicoes.remove("logado");
            definicoes.remove("username");
            definicoes.remove("token");
        }
        definicoes.apply();
    }


    protected void iniciar_intente_extras(Intent intente) {
        intente.putExtra("grupo_selecionado", grupo_selecionado);
        intente.putExtra("logado", logado);

        if (logado) {
            intente.putExtra("username", username);
            intente.putExtra("token", token);
        }

        intente.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        startActivityForResult(intente, 1);
    }


    protected boolean verificar_ligacao_internet() {
        ConnectivityManager net = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (net.getActiveNetworkInfo() == null || !net.getActiveNetworkInfo().isConnectedOrConnecting()) {
            Toast.makeText(this, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    protected void loading(boolean loading) {
        if (loading) {
            findViewById(R.id.loading_anim_listview).setVisibility(View.VISIBLE);
            findViewById(R.id.listview_dados_api).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_anim_listview).setVisibility(View.GONE);
            findViewById(R.id.listview_dados_api).setVisibility(View.VISIBLE);
        }
    }

}

