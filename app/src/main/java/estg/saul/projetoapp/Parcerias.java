package estg.saul.projetoapp;

import android.os.Bundle;
import android.view.ViewStub;

public class Parcerias extends Base {

    private static final String TAG = "PARCERIAS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.parcerias);
        viewstub.inflate();

    }

}
