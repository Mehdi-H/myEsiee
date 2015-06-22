package themeute_entertainment.eroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class ADE
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private static final String base_url = "https://mvx2.esiee.fr/api/ade.php";
    private final Context context;
    private final SharedPreferences settings;
    private boolean listView_isOnScreen = false;
    private DBController controller;

    // ====================================================================================
    // == Constructeur
    // ====================================================================================

    public ADE(Context context) {
        this.context = context;
        this.controller = new DBController(context);
        this.listView_isOnScreen = false;

        settings = context.getSharedPreferences("SHARED_PREFS", context.MODE_PRIVATE);
    }

    // ====================================================================================
    // == METHODES PRIVÉES
    // ====================================================================================

    /**
     * Retourne un ArrayList d'HashMaps à partir d'une liste d'objets JSON décrivant des salles et
     * leurs disponibilités.
     * @param json
     */
    private static ArrayList<HashMap<String,String>> sallesJSON_to_ArrayList(String json, Context context)
    {
        ArrayList<HashMap<String,String>> arraylist = new ArrayList<HashMap<String, String>>();

        // Create GSON object
        Gson gson = new GsonBuilder().create();

        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(json);

            // If no of array elements is not zero
            if(arr.length() != 0)
            {
                // Loop through each array element, get JSON object
                for (int i = 0 ; i < arr.length() ; i++)
                {
                    HashMap<String,String> map = new HashMap<String,String>();
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);

                    String key = obj.keys().next(); // 1 seul élément par objet JSON
                    map.put("nom", key);

                    // Vérification de la disponibilité :
                    if (obj.getInt(key) == 0) {
                        map.put("dispo", context.getResources().getString(R.string.libre));
                    } else if (obj.getInt(key) > 0) {
                        map.put("dispo", obj.get(key).toString() + " " + context.getResources().getString(R.string.minutes_abbr));
                    } else {
                        // "-1" : ne pas afficher (mais laisser activé ici pour debug) :
                        map.put("dispo", context.getResources().getString(R.string.occupee));
                    }

                    arraylist.add(map);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arraylist;
    }


    // ====================================================================================
    // == METHODES PUBLIQUES
    // ====================================================================================

    /**
     * Fonction de recherche de salles.
     * @param listView      a
     * @param criteres      a
     */
    public void rechSalle(final ListView listView, final TextView noData_textView, final HashMap<String,String> criteres)
    {
        if (listView_isOnScreen) {
            ViewGroupUtils.replaceView(listView, noData_textView);
            listView_isOnScreen = false;
        }
        noData_textView.setText(context.getResources().getString(R.string.loading));


        // === Vérifier si on peut re-filtrer les dernières valeurs chargées ===

        /* final long lastRequestDate = Long.parseLong(settings.getString("rechSalle_lastRequestDate", "-1"));

        // Si la dernière requête a eu lieu il y a moins de 60 secondes :
        if (System.currentTimeMillis() - lastRequestDate < 60000)
        {
            // --- Comparaison des critères ---

            // __ Chargement des critères sauvegardés dans une HashMap __

            HashMap<String,String> lastRequestCriteria = new HashMap<String,String>();

            String criteria_str = settings.getString("rechSalle_lastRequestCriteria", "");
            criteria_str = criteria_str.replace("{", "");
            criteria_str = criteria_str.replace("}", "");

            System.out.println(criteria_str);

            String[] criteria_pairs = criteria_str.split(", ");
            for (int i = 0 ; i < criteria_pairs.length ; i++) {
                String[] keyValue = criteria_pairs[i].split("=");
                lastRequestCriteria.put(keyValue[0], keyValue[1]);
            }

            // __ Comparaison des critères sauvegardés et des nouveaux critères __

            boolean isCompatible = true;
            for (HashMap.Entry<String,String> lastCritere : lastRequestCriteria.entrySet())
            {
                String key = lastCritere.getKey();
                String value = lastCritere.getValue();

                // Si le nouveau critère n'existe pas chez les anciens ou qu'il est différent :
                if (! key.equals("occupied") && (criteres.get(key) == null || ! criteres.get(key).equals(value))) {
                    isCompatible = false;
                    break;
                }
            }

            // __ Si les nouveaux critères sont compatibles à un amincissement __

            if (isCompatible) {
                final String lastRequestResult = settings.getString("rechSalle_lastRequestResult", "");
                processResponse(lastRequestResult, criteres, listView, noData_textView);
                return;
            }
        }*/


        // === Construction de l'URL de requête ===

        String url = base_url + "?func=rechSalle";

        // Critères :
        for (HashMap.Entry<String,String> entry : criteres.entrySet())
        {
            try {
                if (!entry.getValue().startsWith("null")) {
                    url += "&" + entry.getKey() + "=" + entry.getValue();
                }
            } catch (NullPointerException e) {

            }
        }

        // Init :
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // Lancer la requête :
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                processResponse(response, criteres, listView, noData_textView);
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

    public void processResponse(final String response, final HashMap<String,String> criteres, final ListView listView, final TextView noData_textView)
    {
        // === Construire un tableau de Strings qui contient toutes les infos des salles (nom, dispo et caractéristiques) ===

        // --- Récupérer les infos du JSON (nom et dispo des salles correspondantes aux critères de recherche) ---

        ArrayList<HashMap<String,String>> liste_salles_json = sallesJSON_to_ArrayList(response, context);
        if (liste_salles_json.size() != 0)
        {
            // --- Fusionner les infos des salles de la BDD avec la réponse JSON ---

            // ArrayList<HashMap<String,String>> liste_salles_fusionee = new ArrayList<HashMap<String,String>>();
            String liste_salles_fusionee = "";

            for (int i = 0 ; i < liste_salles_json.size() ; i++)
            {
                // Vérifier que l'on veuille les salles occupées :
                if (criteres.get("occupied") != null
                        && criteres.get("occupied").equals("null_noDisplay")
                        && liste_salles_json.get(i).get("dispo").equals(context.getResources().getString(R.string.occupee)))
                {
                    continue;
                }

                // Retrouver la salle en question dans la BDD :
                ArrayList<HashMap<String,String>> liste_salle_bdd = controller.getSalles(liste_salles_json.get(i).get("nom"));
                HashMap<String,String> salle_bdd = liste_salle_bdd.get(0);

                // Récupérer le nom de la salle :
                liste_salles_fusionee += liste_salles_json.get(i).get("nom") + ";";

                // Rajouter les caractéristiques de la BDD dans la String :
                liste_salles_fusionee += salle_bdd.get("type") + ";";
                liste_salles_fusionee += salle_bdd.get("projecteur") + ";";

                int tab = Integer.parseInt(salle_bdd.get("tableau"));
                liste_salles_fusionee += (tab == 1 || tab == 3) ? "1;" : "0;"; // tableauBlanc
                liste_salles_fusionee += (tab == 2 || tab == 3) ? "1;" : "0;"; // tableauNoir

                liste_salles_fusionee += salle_bdd.get("imprimante") + ";";

                int taille = Integer.parseInt(salle_bdd.get("taille"));
                if (0 < taille && taille < 30) {
                    liste_salles_fusionee += "S;";
                } else if (taille < 70) {
                    liste_salles_fusionee += "M;";
                } else {
                    liste_salles_fusionee += "L;";
                }

                // Et enfin, la disponibilité (depuis le JSON) :
                liste_salles_fusionee += liste_salles_json.get(i).get("dispo") + "#";
            }

            String[] liste_finale = liste_salles_fusionee.split("#");


            // --- Peupler la ListView avec la liste des salles obtenue ---

            ViewGroupUtils.replaceView(noData_textView, listView);

            RoomArrayAdapter adapter = new RoomArrayAdapter(
                    context,
                    liste_finale
            );
            listView.setAdapter(adapter);

            listView_isOnScreen = true;

            // --- Sauvegarde de la dernière requête et des critères utilisés ---

            /* SharedPreferences.Editor editor = settings.edit();

            editor.putString("rechSalle_lastRequestResult", response);
            editor.putString("rechSalle_lastRequestCriteria", criteres.toString());
            editor.putString("rechSalle_lastRequestDate", System.currentTimeMillis()+"");
            editor.apply(); */
        }
        else
        {
            if (listView_isOnScreen) {
                ViewGroupUtils.replaceView(listView, noData_textView);
            }
            noData_textView.setText(context.getResources().getString(R.string.noData));
            listView_isOnScreen = false;
        }
    }

    public void dispo(final String Table, final String nom, final String date, final int width, final ImageView imageView)
    {
        // === Dimensions de l'image à générer ===

        double ratio = 1.5;
        int height = (int) ((double)width * ratio);


        // === Construction de l'URL de requête ===

        String url = base_url + "?func=dispo" + Table;
        try {
            url += "&nom=" + URLEncoder.encode(nom, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        url += "&largeur=" + width + "&hauteur=" + height;
        url += date.equals("") ? "" : "&date=" + date;

        // Toast.makeText(context, url, Toast.LENGTH_SHORT).show();

        // Init :
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // Lancer la requête :
        client.post(url, params, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response)
            {
                Bitmap imageET_bitmap = BitmapFactory.decodeFile(response.getAbsolutePath());
                imageView.setImageBitmap(imageET_bitmap);
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
}
