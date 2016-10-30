package estg.saul.projetoapp;

import android.os.Bundle;
import android.view.ViewStub;

public class Eventos extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub)findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.content_eventos);
        viewstub.inflate();
    }

}