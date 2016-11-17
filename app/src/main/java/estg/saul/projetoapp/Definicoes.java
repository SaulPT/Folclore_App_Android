package estg.saul.projetoapp;


import android.os.Bundle;
import android.preference.PreferenceFragment;
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

    //NECESSÀRIO USAR A CLASSE "frangemt"
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
