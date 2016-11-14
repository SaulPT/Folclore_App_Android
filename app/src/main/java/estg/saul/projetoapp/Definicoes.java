package estg.saul.projetoapp;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;


public class Definicoes extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.definicoes);


        getFragmentManager().beginTransaction().replace(android.R.id.content, new Definicoes_Fragment()).commit();
    }


    //CRIAR AQUI UMA TOOLBAR

    public static class Definicoes_Fragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.definicoes);
        }
    }


}
