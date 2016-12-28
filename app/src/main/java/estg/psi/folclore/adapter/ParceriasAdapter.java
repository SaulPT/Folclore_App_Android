package estg.psi.folclore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import estg.psi.folclore.model.Parceria;


public class ParceriasAdapter extends ArrayAdapter<Parceria> {

    private final Context context;
    private final List<Parceria> parcerias;

    public ParceriasAdapter(Context context, int layout, List<Parceria> parcerias) {
        super(context, layout, parcerias);
        this.parcerias = parcerias;
        this.context = context;
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Parceria parceria = parcerias.get(position);

        return BaseAdapter.getview_generico(context, convertView, parent,
                parceria.parceiro, parceria.site_parceiro, parceria.descricao, parceria.imagem, "parcerias/");
    }
}

