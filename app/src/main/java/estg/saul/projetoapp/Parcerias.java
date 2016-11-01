package estg.saul.projetoapp;

import android.os.Bundle;
import android.view.ViewStub;

/**
 * Created by SaulPT on 01/11/2016.
 */

public class Parcerias extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.parcerias);
        viewstub.inflate();

        checkar_estado_navigation_view();
    }

}
