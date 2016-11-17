package estg.saul.projetoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class Base extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //VARIAVEL GLOBAL PARA SABER O GRUPO SELECCIONADO?????
    //VS
    //SHARED PREFERENCES??????
    protected String grupo_selecionado;
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
        //drawer.setDrawerListener(toggle);
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
        checkar_estado_grupo_login();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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


        //MOSTRA A PESQUISA EM CERTAS ATIVIDADES
        if (getClass().getSimpleName().equals("Login") ||
                getClass().getSimpleName().equals("AreaPessoal")) {
            menu.findItem(R.id.action_search).setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intente = null;

        switch (item.getItemId()) {
            case R.id.action_definicoes:
                intente = new Intent("definicoes");

                break;
            case R.id.action_area_pessoal:
                if (logado) {
                    intente = new Intent("area_pessoal");
                } else {
                    intente = new Intent("login");
                }
                break;
            case R.id.action_logout:
                logado = false;

                //GUARDA NAS DEFINIÇÕES O ESTADO DO LOGIN
                guardar_definicoes_logado(logado);


                if (getClass().getSimpleName().equals("AreaPessoal")) {
                    intente = new Intent("login");
                }
                break;
        }


        if (intente != null) {
            intente.putExtra("grupo_selecionado", grupo_selecionado);
            intente.putExtra("logado", logado);

            intente.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            startActivity(intente);
        } else {
            //PARA VOLTAR A CHAMAR A FUNÇÃO QUE CRIA O MENU (onCreateOptionsMenu)
            invalidateOptionsMenu();
            checkar_estado_grupo_login();
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Intent intente = new Intent();

        switch (item.getItemId()) {
            case R.id.nav_noticias:
                intente = new Intent("noticias");
                break;
            case R.id.nav_eventos:
                intente = new Intent("eventos");
                break;
            case R.id.parcerias:
                intente = new Intent("parcerias");
                break;
            case R.id.nav_grupos:
                intente = new Intent("grupos");
                break;
            default:
                intente = new Intent("grupo_info");
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        intente.putExtra("grupo_selecionado", grupo_selecionado);
        intente.putExtra("logado", logado);

        intente.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        startActivity(intente);

        return true;
    }


    protected void checkar_estado_grupo_login() {
        //DEFINE O ESTADO DOS ITEMS DO MENU DO GRUPO COM BASE NA VARIAVEL GLOBAL
        SharedPreferences definicoes = PreferenceManager.getDefaultSharedPreferences(this);
        if (getClass().getSimpleName().equals("HomeNoticias") && definicoes.getBoolean("grupo_auto", false)) {
            definicoes.edit().putBoolean("grupo_auto", false).apply();
        } else {
            grupo_selecionado = getIntent().getStringExtra("grupo_selecionado");
        }


        Menu m = ((NavigationView) findViewById(R.id.nav_view)).getMenu();
        if (grupo_selecionado == null) {
            m.setGroupEnabled(R.id.menu_grupo, false);
        } else {
            m.setGroupEnabled(R.id.menu_grupo, true);
        }

        //PARA VOLTAR A CHAMAR A FUNÇÃO QUE CRIA O MENU (onCreateOptionsMenu)
        if (!logado) {
            logado = getIntent().getBooleanExtra("logado", false);
        }
        View navview = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        TextView txtusername = (TextView) navview.findViewById(R.id.txt_username);
        if (logado) {
            txtusername.setText("username");
        } else {
            txtusername.setText("desconhecido");
        }
        invalidateOptionsMenu();

    }


    protected boolean guardar_definicoes_logado(boolean estado_login) {
        SharedPreferences.Editor definicoes = PreferenceManager.getDefaultSharedPreferences(this).edit();
        definicoes.putBoolean("logado", estado_login);
        return definicoes.commit();
    }

}

