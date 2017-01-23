package com.hdevteam.impiccato.Gioco.Wordlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Marco on 22/01/2017.
 */

public class WordlistHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "Words";
    public static final int DB_VERSION = 1;
    private Context context;

    public static String EASY_WORDS_TABLE = "easywords";
    public static String MEDIUM_WORDS_TABLE = "mediumwords";
    public static String HARD_WORDS_TABLE = "hardwords";
    public static String ID_FIELD = "ID";
    public static String WORD_FIELD = "Word";
    public static String MEANING_FIELD = "Meaning";
    public static String CATEGORY_FIELD = "Category";

    private int hardWords;
    private int mediumWords;
    private int easyWords;

    public int getHardWords() {
        return hardWords;
    }

    public int getMediumWords() {
        return mediumWords;
    }

    public int getEasyWords() {
        return easyWords;
    }

    public WordlistHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        easyWords = getWordsCount(EASY_WORDS_TABLE);
        mediumWords = getWordsCount(MEDIUM_WORDS_TABLE);
        hardWords = getWordsCount(HARD_WORDS_TABLE);

    }
    /**Restituisce il numero di parole presenti nella tabella
     * @param tableName nome della tabella*/
    private int getWordsCount(String tableName){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        cursor.moveToFirst();
        return cursor.getCount();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i("WordlistHelper", "Creo il db...");
        createTable(EASY_WORDS_TABLE, database);
        createTable(MEDIUM_WORDS_TABLE, database);
        createTable(HARD_WORDS_TABLE, database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EASY_WORDS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MEDIUM_WORDS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HARD_WORDS_TABLE);
        onCreate(sqLiteDatabase);
    }
    /**
     * Crea la tabella con il nome indicato come parametro
     * @param tableName nome della tabella da creare
     * @param database database nel quale creare la tabella*/
    private String createTable(String tableName, SQLiteDatabase database){
        String creationQuery = String.format("CREATE TABLE %s (" +
                                             "%s VARCHAR(6) NOT NULL PRIMARY KEY," +
                                             "%s VARCHAR(20) NOT NULL," +
                                             "%s VARCHAR(150)," +
                                             "%s VARCHAR(30));",
                                            tableName, ID_FIELD, WORD_FIELD, MEANING_FIELD, CATEGORY_FIELD);

        Log.i("WordlistHelper", "Query: " + creationQuery);
        database.execSQL(creationQuery);
        insertDataIntoTable(tableName, database);
        return creationQuery;
    }

    /**Esegue la query contenuta negli assets della tabella indicata come parametro
     * @param tableName nome della tabella in cui inserire i dati
     * @param database database nel quale si trova la tabella*/
    private void insertDataIntoTable(String tableName, SQLiteDatabase database) {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(tableName + ".sql")));
            String row = reader.readLine();
            StringBuilder query = new StringBuilder();
            while(row != null){
                query.append(row);
                row = reader.readLine();
            }
            database.execSQL(query.toString());
        }catch(IOException ex) {
            Log.e("WordlistHelper", "Errore creazione tabella: " + ex.getLocalizedMessage());
        }
        catch(SQLiteException e){
            Log.e("WordlistHelper", "Errore esecuzione query: " + e.getLocalizedMessage());
        }
    }
    /**
     * Restituisce la parola identificata dall'id
     * @param id id della parola
     * @param table nome della tabella dalla quale ricavare la parola
     */
    public String getWord(long id, String table){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + WORD_FIELD + " FROM " + table + " WHERE " + ID_FIELD + " = " + id;
        Log.i("WordlistHelper", "Query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(WORD_FIELD));
    }
    /**
     * Restituisce il significato della parola identificata dall'id
     * @param id id della parola
     * @param table nome della tabella dalla quale ricavare la parola*/
    public String getMeaning(long id, String table){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + MEANING_FIELD + " FROM " + table + " WHERE " + ID_FIELD + "  = " + id;
        Log.i("WordlistHelper", "Query: " + query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(MEANING_FIELD));
    }
}
