package estg.saul.appmockup;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.RelativeLayout;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    //LAYOUT PRINCIPAL DA ACTIVITY
    CoordinatorLayout layout_principal;
    //
    //VIEW COM O LAYOUT DE CONTEUDO
    View conteudo;
    //



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //DEFINE O CONTEUDO NOTICIAS NO ARRANQUE
        layout_principal = ((CoordinatorLayout)findViewById(R.id.app_bar_layout));
        conteudo = View.inflate(this,R.layout.noticias,null);

        layout_principal.addView(conteudo);
        //
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        layout_principal.removeView(conteudo);

        switch (item.getItemId()){
            case R.id.action_definicoes:
                break;
            case R.id.action_area_pessoal:
                conteudo=View.inflate(this,R.layout.area_pessoal,null);
                break;
        }

        layout_principal.addView(conteudo);


        return super.onOptionsItemSelected(item);
    }






    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        layout_principal.removeView(conteudo);

        switch (item.getItemId()){
            case R.id.nav_noticias:
                conteudo=View.inflate(this,R.layout.noticias,null);
                break;
            case R.id.nav_eventos:
                conteudo=View.inflate(this,R.layout.eventos,null);
                break;
            case R.id.nav_parcerias:
                conteudo=View.inflate(this,R.layout.parcerias,null);
                break;
            case R.id.nav_ranchos:
                conteudo=View.inflate(this,R.layout.ranchos,null);
                break;
        }

        layout_principal.addView(conteudo);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
