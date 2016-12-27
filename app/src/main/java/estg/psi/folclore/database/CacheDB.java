package estg.psi.folclore.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import estg.psi.folclore.model.Noticia;

public class CacheDB extends SQLiteOpenHelper {

    public static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final int VERSAO = 1;
    private static final String NOME = "folclore.db";

    public CacheDB(Context context) {
        super(context, NOME, null, VERSAO);
    }

    public void onCreate(SQLiteDatabase db) {
        String comando = "CREATE TABLE IF NOT EXISTS noticia" +
                " (id INT PRIMARY KEY," +
                " titulo TEXT," +
                " conteudo TEXT," +
                " data_criacao TEXT," +
                " data_edicao TEXT," +
                " autor_id INT," +
                " imagem TEXT," +
                " ativo INT," +
                " aprovado INT)";
        db.execSQL(comando);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //CODIGO EXECUTADO QUANDO SE ALTERA A VERSÃO
        onCreate(db);
    }

    public void inserir_noticias(List<Noticia> noticias) {
        //AO FUNCIONAR COMO CACHE, A BD APAGA TUDO
        getWritableDatabase().delete("noticia", null, null);

        //E INSERE OS DADOS QUE RECEBEU DA API
        for (Noticia noticia : noticias) {
            ContentValues valores = new ContentValues();
            valores.put("id", noticia.id);
            valores.put("titulo", noticia.titulo);
            valores.put("conteudo", noticia.conteudo);
            valores.put("data_criacao", dateformat.format(noticia.data_criacao));
            valores.put("data_edicao", dateformat.format(noticia.data_edicao));
            valores.put("autor_id", noticia.autor_id);
            valores.put("imagem", noticia.imagem);
            valores.put("ativo", noticia.ativo);
            valores.put("aprovado", noticia.aprovado);

            getWritableDatabase().insert("noticia", null, valores);
        }
    }

    public List<Noticia> obter_noticias() {
        Cursor query_cursor = getReadableDatabase().query("noticia", null, "ativo = 1", null, null, null, null);

        List<Noticia> noticias_list = new ArrayList<>();
        for (query_cursor.moveToFirst(); !query_cursor.isAfterLast(); query_cursor.moveToNext()) {
            Noticia noticia = new Noticia();
            noticia.id = query_cursor.getInt(query_cursor.getColumnIndex("id"));
            noticia.titulo = query_cursor.getString(query_cursor.getColumnIndex("titulo"));
            noticia.conteudo = query_cursor.getString(query_cursor.getColumnIndex("conteudo"));

            //O ANDROID OBRIGA TRATAR A EXCEPÇÃO QUE RESULTA DA CONVERSÃO DO TEXTO EM DATA
            try {
                String data = query_cursor.getString(query_cursor.getColumnIndex("data_criacao"));
                noticia.data_criacao = dateformat.parse(data);
                data = query_cursor.getString(query_cursor.getColumnIndex("data_edicao"));
                noticia.data_edicao = dateformat.parse(data);
            } catch (ParseException e) {
                Log.e("DateParse", e.getMessage());
            }

            noticia.autor_id = query_cursor.getInt(query_cursor.getColumnIndex("autor_id"));
            noticia.imagem = query_cursor.getString(query_cursor.getColumnIndex("imagem"));
            noticia.ativo = query_cursor.getInt(query_cursor.getColumnIndex("ativo"));
            noticia.aprovado = query_cursor.getInt(query_cursor.getColumnIndex("aprovado"));
            noticias_list.add(noticia);
        }
        query_cursor.close();

        //PARA ORDENAR AS NOTICIAS POR ORDEM DECRESCENTE DA DATA
        Collections.sort(noticias_list, new Comparator<Noticia>() {
            @Override
            public int compare(Noticia n1, Noticia n2) {
                if (n1.data_edicao.after(n2.data_edicao)) {
                    return -1;
                } else if (n1.data_edicao.before(n2.data_edicao)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return noticias_list;
    }

    public void apagar_noticias() {
        getWritableDatabase().delete("noticia", null, null);
    }

}
