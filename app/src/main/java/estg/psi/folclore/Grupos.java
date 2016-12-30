package estg.psi.folclore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Grupo;

public class Grupos extends Base {

    private Button button_remover_grupo;

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
                grupo_selecionado = Grupo.ordenar_nome(bd.obter_grupos()).get((int) id).id;
                bd.close();

                Intent intente = new Intent("estg.psi.folclore.GRUPOINFO");
                iniciar_intente_extras(intente);
            }
        });


        button_remover_grupo = (Button) findViewById(R.id.button_remover_grupo_selecionado);
        button_remover_grupo.setVisibility(View.VISIBLE);
        button_remover_grupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grupo_selecionado = -1;
                atualizar_nav_header_action_menu();
                v.setEnabled(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getIntent().getIntExtra("grupo_selecionado", -1) == -1) {
            button_remover_grupo.setEnabled(false);
        } else {
            button_remover_grupo.setEnabled(true);
        }
        obter_dados_API_array("grupos");
    }

}
