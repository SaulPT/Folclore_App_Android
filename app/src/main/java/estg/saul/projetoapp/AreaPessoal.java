package estg.saul.projetoapp;

import android.os.Bundle;
import android.view.ViewStub;

/**
 * Created by SaulPT on 04/11/2016.
 */

public class AreaPessoal extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.areapessoal);
        viewstub.inflate();

    }
}
