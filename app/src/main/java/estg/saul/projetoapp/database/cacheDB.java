package estg.saul.projetoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import estg.saul.projetoapp.model.Noticia;

/**
 * Created by SaulPT on 06/12/2016.
 */

public class CacheDB extends SQLiteOpenHelper {

    private static int VERSAO = 1;
    private static String NOME = "folclore.db";


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
                " ativo INT)";
        db.execSQL(comando);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //CODIGO EXECUTADO QUANDO SE ALTERA A VERSÃO
        onCreate(db);
    }

    public void inserir_noticias(List<Noticia> noticias){
        //AO FUNCIONAR COMO CACHE, A BD APAGA TUDO
        getWritableDatabase().delete("noticia",null,null);

        //E INSERE OS DADOS QUE RECEBEU DA API
        for (Noticia noticia : noticias) {
            ContentValues valores = new ContentValues();
            valores.put("id",noticia.id);
            valores.put("titulo",noticia.titulo);
            valores.put("conteudo",noticia.conteudo);
            valores.put("data_criacao",noticia.data_criacao.toString());
            valores.put("data_edicao",noticia.data_edicao.toString());
            valores.put("autor_id",noticia.autor_id);
            valores.put("imagem",noticia.imagem);
            valores.put("ativo",noticia.ativo);

            getWritableDatabase().insert("noticia",null,valores);
        }
    }

    public Cursor obter_noticias(){
        return getReadableDatabase().query("noticia",null,"ativo = 1",null,null,null,null);
    }

}
