package estg.psi.folclore;

import android.os.Bundle;
import android.view.ViewStub;

public class Eventos extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.noticias_parcerias_eventos);
        viewstub.inflate();
    }

    @Override
    public void onResume() {
        super.onResume();

        obter_dados_API("eventos", "GET", "/eventos");
    }

}
