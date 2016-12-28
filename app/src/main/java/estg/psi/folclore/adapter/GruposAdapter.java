package estg.psi.folclore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import estg.psi.folclore.model.Grupo;


public class GruposAdapter extends ArrayAdapter<Grupo> {

    private final Context context;
    private final List<Grupo> grupos;

    public GruposAdapter(Context context, int layout, List<Grupo> grupos) {
        super(context, layout, grupos);
        this.grupos = grupos;
        this.context = context;
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Grupo grupo = grupos.get(position);

        return BaseAdapter.getview_generico(context, convertView, parent,
                grupo.abreviatura, "concelho: " + grupo.concelho_id, null, grupo.logo, "grupos/");
    }
}

