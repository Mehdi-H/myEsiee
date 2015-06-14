package themeute_entertainment.eroom;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Source : http://programmerguru.com/android-tutorial/how-to-sync-remote-mysql-db-to-sqlite-on-android/#disqus_thread
 */

public class DBController extends SQLiteOpenHelper
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private final String base_url = "https://mvx2.esiee.fr/api/";
    private final String hash_version_url = base_url + "bdd.php?func=getHashVersion";

    private boolean[] syncedTable = new boolean[2];

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

    public ArrayList<HashMap<String, String>> getDataFromTable(final String table, final String nom)
    {
        if (table.equals("salle")) {
            return getSalles(nom);
        } else if (table.equals("prof")) {
            return getProfs(nom);
        } else {
            return null;
        }
    }

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
    public ArrayList<HashMap<String, String>> getProfs(final String nom)
    {
        ArrayList<HashMap<String,String>> liste_profs = new ArrayList<HashMap<String,String>>();

        String selectQuery;
        if (nom.equals("all")) {
            selectQuery = "SELECT * FROM prof";
        } else {
            selectQuery = "SELECT * FROM prof WHERE nom='"+nom+"'";
        }

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

    // ====================================================================================
    // == Verifs
    // ====================================================================================

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

    // ====================================================================================
    // == MISE A JOUR
    // ====================================================================================


    public void updateIfNeeded(String local_hash, String online_hash, ProgressDialog prgDialog, SharedPreferences settings, Context context)
    {
        System.out.println("online_hash : " + online_hash);
        if (! local_hash.equals(online_hash)) {
            System.out.println("updateing...");
            syncSQLiteMySQLDB(online_hash, prgDialog, settings, context);
        }
    }

    public void checkForUpdates(final ProgressDialog prgDialog, final SharedPreferences settings, final Context context)
    {
        System.out.println("Checking for updates...");
        // === Récupérer le numéro de version enregistré ===

        final String local_hash = settings.getString("hash_version", "none");
        System.out.println("local : " + local_hash);

        // === Récupérer le numéro de version en ligne ===

        final String[] online_hash = new String[1];

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.post(hash_version_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                updateIfNeeded(local_hash, response, prgDialog, settings, context);
            }

            // When error occurred
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    System.out.println(statusCode + " - Requested resource not found : " + content + "\n" + error.toString());
                } else if (statusCode == 500) {
                    System.out.println(statusCode + " - Something went wrong at server end : " + content + "\n" + error.toString());
                } else {
                    System.out.println(statusCode + " - Unexpected Error occurred ! : " + content + "\n" + error.toString());
                }
            }
        });
    }

    // ====================================================================================
    // -- Gets
    // ====================================================================================

    public String[] getNoms(final String table)
    {
        // Retrouver la salle en question dans la BDD :
        ArrayList<HashMap<String,String>> liste_bdd = getDataFromTable(table, "all");

        String[] noms = new String[liste_bdd.size()];

        for (int i = 0 ; i < liste_bdd.size() ; i++) {
            noms[i] = liste_bdd.get(i).get("nom");
        }

        return noms;
    }


    // ====================================================================================
    // -- Synchronisation MySQL serveur -> SQLite Android
    // ====================================================================================

    /**
     * Method to Sync MySQL to SQLite DB
     */
    public void syncSQLiteMySQLDB(String new_hash, ProgressDialog prgDialog, SharedPreferences settings, Context context)
    {
        System.out.println("Syncing...");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // Show ProgressBar
        prgDialog.show();

        // On supprime toute la BDD locale et on la reconstruit :
        onUpgrade(getWritableDatabase(), 0, 1);

        // Puis on la rempli :
        syncedTable[0] = false;
        syncedTable[1] = false;
        requestData(client, params, "salle", context, prgDialog);
        requestData(client, params, "prof", context, prgDialog);

        // Et on met à jour la version de la BDD locale :
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("hash_version", new_hash);
        editor.apply();
    }

    public void requestData(AsyncHttpClient client, RequestParams params, final String table, final Context context, final ProgressDialog prgDialog)
    {
        System.out.println("requestData de " + table);
        client.post("https://mvx2.esiee.fr/mysql_sync/getdata.php?table=" + table, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response)
            {
                updateSQLite(response, table, context);
                System.out.println("requestData de " + table + " terminé");

                // Hide ProgressBar
                if (prgDialog != null) {
                    prgDialog.hide();
                }
            }

            // When error occurred
            @Override
            public void onFailure(int statusCode, Throwable error, String content)
            {
                System.out.println("requestData de " + table + " fail !");

                // Hide ProgressBar
                if (syncedTable[0]) {
                    prgDialog.hide();
                }

                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet] : " + statusCode,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Met à jour la BDD SQLite Android
     * @param response
     */
    public void updateSQLite(String response, String table, Context context)
    {
        HashMap<String,String> queryValues;

        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            // If no of array elements is not zero
            if(arr.length() != 0)
            {
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0 ; i < arr.length() ; i++)
                {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);

                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String,String>();

                    if (table.equals("salle")) {
                        queryValues.put("nom", obj.get("nom").toString());
                        queryValues.put("resourceID", obj.get("resourceID").toString());
                        queryValues.put("type", obj.get("type").toString());
                        queryValues.put("taille", obj.get("taille").toString());
                        queryValues.put("projecteur", obj.get("projecteur").toString());
                        queryValues.put("tableau", obj.get("tableau").toString());
                        queryValues.put("imprimante", obj.get("imprimante").toString());

                        insertSalle(queryValues);
                        syncedTable[0] = true;

                    } else if (table.equals("prof")) {
                        queryValues.put("nom", obj.get("nom").toString());
                        queryValues.put("resourceID", obj.get("resourceID").toString());
                        queryValues.put("bureau", obj.get("bureau").toString());
                        queryValues.put("email", obj.get("email").toString());

                        insertProf(queryValues);
                        syncedTable[1] = true;
                    }
                }

                // Synchro terminée :
                if (syncedTable[0] && syncedTable[1]) {
                    Toast.makeText(context, "La base de données a été mise à jour", Toast.LENGTH_SHORT).show();
                    reloadActivity(context);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void reloadActivity(Context context) {
        Intent objIntent = new Intent(context, RechSalle.class);
        objIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(objIntent);
    }

}