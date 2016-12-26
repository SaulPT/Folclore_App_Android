package estg.psi.folclore;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;


public class Definicoes extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_ab_back_material);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.definicoes);
        viewstub.inflate();


        //SUBSTITUI O LAYOUT DE CONTEUDO COM OS ITEMS DAS DEFINICOES OBTIDOS PELO PREFERENCEFRAGMENT
        getFragmentManager().beginTransaction().replace(viewstub.getInflatedId(), new Definicoes_Fragment()).commit();

    }

    @Override
    public void onBackPressed() {
        SharedPreferences definicoes = PreferenceManager.getDefaultSharedPreferences(this);
        if (!definicoes.getBoolean("guardar_grupo_selecionado", false)) {
            definicoes.edit().remove("grupo_selecionado_nome").apply();
        }

        super.onBackPressed();
    }

    //NECESSÀRIO USAR A CLASSE "fragment"
    //PORQUE O MÉTODO "addPreferencesFromResource" DEXOU SE SER
    //SUPORTADO NA CLASS "PreferenceActivity"
    public static class Definicoes_Fragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.definicoes);
        }
    }
}
