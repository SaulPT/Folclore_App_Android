package estg.psi.folclore;

import android.os.Bundle;
import android.view.ViewStub;

public class Parcerias extends Base {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub viewstub = (ViewStub) findViewById(R.id.viewstub);
        viewstub.setLayoutResource(R.layout.listview_dados_api);
        viewstub.inflate();
    }

    @Override
    public void onResume() {
        super.onResume();

        obter_dados_API_array("parcerias");
    }

}
