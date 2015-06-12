package themeute_entertainment.eroom;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Source : http://programmerguru.com/android-tutorial/how-to-sync-remote-mysql-db-to-sqlite-on-android/#disqus_thread
 */

public class DBController extends SQLiteOpenHelper
{

    // ====================================================================================
    // == CONSTRUCTEUR
    // ====================================================================================

    public DBController(Context applicationcontext) {
        super(applicationcontext, "eroom.db", null, 1);
    }

    // ====================================================================================
    // == DATA DEFINITION LANGUAGE
    // ====================================================================================

    /**
     * Crée les tables "salle" et "prof" de la BDD la première fois.
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        // Création de la table "salle" :
        String query = "CREATE TABLE salle (" +
            "nom TEXT PRIMARY KEY," +
            "resourceID INTEGER UNIQUE DEFAULT NULL," +
            "type TEXT," +
            "taille INTEGER NOT NULL DEFAULT 0," +
            "projecteur INTEGER NOT NULL DEFAULT 0," +
            "tableau INTEGER NOT NULL DEFAULT 0," +
            "imprimante INTEGER NOT NULL DEFAULT 0" +
        ")";
        database.execSQL(query);

        // Création de la table "prof" :
        query = "CREATE TABLE prof (" +
            "nom TEXT PRIMARY KEY," +
            "resourceID INTEGER UNIQUE DEFAULT NULL," +
            "bureau TEXT DEFAULT NULL," +
            "email TEXT DEFAULT NULL" +
        ")";
        database.execSQL(query);
    }

    /**
     * Supprime les tables "prof" et "salle" puis appelle la fonction onCreate().
     * @param database
     * @param version_old
     * @param current_version
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {
        // Supprimer la table "salle" :
        String query = "DROP TABLE IF EXISTS salle";
        database.execSQL(query);

        // Supprimer la table "prof" :
        query = "DROP TABLE IF EXISTS prof";
        database.execSQL(query);

        // Les recréer :
        onCreate(database);
    }

    // ====================================================================================
    // == DATA MANIPULATION LANGUAGE
    // ====================================================================================

    /**
     * Insert des salles dans la BDD SQLite.
     * @param queryValues
     */
    public void insertSalle(HashMap<String,String> queryValues)
    {
        // Récupérer la BDD :
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Insérer les valeurs :
        values.put("nom", queryValues.get("nom"));
        values.put("resourceID", queryValues.get("resourceID"));
        values.put("type", queryValues.get("type"));
        values.put("taille", queryValues.get("taille"));
        values.put("projecteur", queryValues.get("projecteur"));
        values.put("tableau", queryValues.get("tableau"));
        values.put("imprimante", queryValues.get("imprimante"));

        database.insert("salle", null, values);

        // Fermer la BDD :
        database.close();
    }

    /**
     * Insert des profs dans la BDD SQLite.
     * @param queryValues
     */
    public void insertProf(HashMap<String,String> queryValues)
    {
        // Récupérer la BDD :
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Insérer les valeurs :
        values.put("nom", queryValues.get("nom"));
        values.put("resourceID", queryValues.get("resourceID"));
        values.put("bureau", queryValues.get("bureau"));
        values.put("email", queryValues.get("email"));

        database.insert("prof", null, values);

        // Fermer la BDD :
        database.close();
    }

    // ====================================================================================
    // == DATA QUERY LANGUAGE
    // ====================================================================================

    /**
     * Obtenir la liste des salles de la BDD SQLite dans un ArrayList.
     * @return
     */
    public ArrayList<HashMap<String, String>> getSalles(String nom)
    {
        ArrayList<HashMap<String,String>> liste_salles = new ArrayList<HashMap<String,String>>();

        String selectQuery;
        if (nom.equals("all")) {
            selectQuery = "SELECT * FROM salle";
        } else {
            selectQuery = "SELECT * FROM salle WHERE nom='"+nom+"'";
        }

        // Exécuter la requête :
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String,String> map = new HashMap<String,String>();

                map.put("nom", cursor.getString(0));
                map.put("resourceID", cursor.getString(1));
                map.put("type", cursor.getString(2));
                map.put("taille", cursor.getString(3));
                map.put("projecteur", cursor.getString(4));
                map.put("tableau", cursor.getString(5));
                map.put("imprimante", cursor.getString(6));

                liste_salles.add(map);
            } while (cursor.moveToNext());
        }

        database.close();
        return liste_salles;
    }

    /**
     * Obtenir la liste des profs de la BDD SQLite dans un ArrayList.
     * @return
     */
    public ArrayList<HashMap<String, String>> getProfs()
    {
        ArrayList<HashMap<String,String>> liste_profs = new ArrayList<HashMap<String,String>>();

        String selectQuery = "SELECT * FROM prof";

        // Exécuter la requête :
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String,String> map = new HashMap<String,String>();

                map.put("nom", cursor.getString(0));
                map.put("resourceID", cursor.getString(1));
                map.put("bureau", cursor.getString(2));
                map.put("email", cursor.getString(3));

                liste_profs.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return liste_profs;
    }

    public boolean existsIn(String nom, String table)
    {
        String selectQuery = "SELECT nom FROM " + table + " WHERE nom='" + nom + "'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        boolean exists = false;
        if (cursor.moveToFirst()) {
            do {
                exists = true;
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return exists;
    }
}