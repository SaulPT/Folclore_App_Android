package estg.saul.appmockup;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //LAYOUT PRINCIPAL DA ACTIVITY
    CoordinatorLayout layout_principal;

    //VIEW COM O LAYOUT DE CONTEUDO
    View conteudo;

    //OBTEM ESTAS VIEW COMO GLOBAIS PARA MANIPULAR DEPOIS
    Menu action_menu;
    NavigationView navigationView;


    Boolean logado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //DEFINE O CONTEUDO NOTICIAS NO ARRANQUE
        layout_principal = ((CoordinatorLayout) findViewById(R.id.app_bar_layout));
        conteudo = View.inflate(getApplicationContext(), R.layout.noticias, null);

        layout_principal.addView(conteudo);
        //

        logado = false;


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
        getMenuInflater().inflate(R.menu.home_page, menu);


        //PARA OBTER O MENU DA ACTION BAR
        action_menu = menu;
        //


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        layout_principal.removeView(conteudo);

        switch (item.getItemId()) {
            case R.id.action_definicoes:
                break;

            case R.id.action_area_pessoal:
                if (logado) {
                    conteudo = View.inflate(getApplicationContext(), R.layout.area_pessoal, null);
                    layout_principal.addView(conteudo);
                } else {
                    conteudo = View.inflate(getApplicationContext(), R.layout.login, null);
                    layout_principal.addView(conteudo);

                    ((Button) conteudo.findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout_principal.removeView(conteudo);
                            conteudo = View.inflate(getApplicationContext(), R.layout.area_pessoal, null);
                            layout_principal.addView(conteudo);

                            action_menu.findItem(R.id.action_terminar_sessao).setVisible(true);
                            logado = true;
                        }
                    });
                }
                break;

            case R.id.action_terminar_sessao:
                if (conteudo.getId() == View.inflate(getApplicationContext(), R.layout.area_pessoal, null).getId()) {
                    layout_principal.removeView(conteudo);
                    conteudo = View.inflate(getApplicationContext(), R.layout.login, null);
                }
                layout_principal.addView(conteudo);
                item.setVisible(false);
                logado = false;
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        layout_principal.removeView(conteudo);

        switch (item.getItemId()) {
            case R.id.menu_noticias:
                conteudo = View.inflate(getApplicationContext(), R.layout.noticias, null);
                layout_principal.addView(conteudo);
                break;
            case R.id.menu_eventos:
                conteudo = View.inflate(getApplicationContext(), R.layout.eventos, null);
                layout_principal.addView(conteudo);
                break;
            case R.id.menu_parcerias:
                conteudo = View.inflate(getApplicationContext(), R.layout.parcerias, null);
                layout_principal.addView(conteudo);
                break;
            case R.id.menu_ranchos:
                conteudo = View.inflate(getApplicationContext(), R.layout.ranchos, null);
                layout_principal.addView(conteudo);

                //AÇÃO DO BOTAO DUMA VISTA CARREGADA POSTERIORMENTE
                ((Button) conteudo.findViewById(R.id.btn_rancho_x)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigationView.getMenu().setGroupEnabled(R.id.grupo_menus_rancho, true);

                        layout_principal.removeView(conteudo);
                        conteudo = View.inflate(getApplicationContext(), R.layout.rancho_inicio, null);
                        layout_principal.addView(conteudo);
                    }
                });
                //

                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
