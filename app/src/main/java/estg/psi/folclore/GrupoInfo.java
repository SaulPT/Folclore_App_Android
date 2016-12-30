package estg.psi.folclore;

import android.os.Bundle;
import android.view.ViewStub;

public class GrupoInfo extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.item_listview);
        viewstub.inflate();


    }

    @Override
    public void onResume() {
        super.onResume();

        if (getIntent() != null) {
            obter_dados_API_objeto("grupos/", getIntent().getIntExtra("grupo_selecionado", -1));
        }
    }

}
