package estg.psi.folclore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import estg.psi.folclore.database.CacheDB;
import estg.psi.folclore.model.Evento;


public class EventosAdapter extends ArrayAdapter<Evento> {

    private final Context context;
    private final List<Evento> eventos;

    public EventosAdapter(Context context, int layout, List<Evento> eventos) {
        super(context, layout, eventos);
        this.eventos = eventos;
        this.context = context;
    }

    @Override
    public
    @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Evento evento = eventos.get(position);

        return BaseAdapter.getview_generico(context, convertView, parent,
                evento.nome, CacheDB.dateformat.format(evento.data), evento.descricao, evento.imagem, "eventos/");
    }
}

