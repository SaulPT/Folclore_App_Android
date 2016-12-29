package estg.psi.folclore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;

import estg.psi.folclore.database.CacheDB;

public class Grupos extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.listview_dados_api);
        viewstub.inflate();

        ((ListView) findViewById(R.id.listview_dados_api)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CacheDB bd = new CacheDB(Grupos.this);
                grupo_selecionado = bd.obter_grupos().get((int) id).id;
                bd.close();

                Intent intente = new Intent("estg.psi.folclore.GRUPOINFO");
                iniciar_intente_extras(intente);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        obter_dados_API_listview("GET", "grupos");
    }

}
