package themeute_entertainment.eroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Byakko on 11/06/2015.
 */
public class ADE
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private static final String base_url = "https://mvx2.esiee.fr/api/ade.php";


    // ====================================================================================
    // == METHODES PRIVÉES
    // ====================================================================================

    /**
     * Retourne un ArrayList d'HashMaps à partir d'une liste d'objets JSON décrivant des salles et
     * leurs disponibilités.
     * @param json
     */
    private static ArrayList<HashMap<String,String>> sallesJSON_to_ArrayList(String json)
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
                        map.put("dispo", "Libre");
                    } else if (obj.getInt(key) > 0) {
                        map.put("dispo", obj.get(key).toString() + " min");
                    } else {
                        // "-1" : ne pas afficher (mais laisser activé ici pour debug) :
                        map.put("dispo", "Occupé");
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
     * @param controller    a
     * @param context       a
     */
    public static void rechSalle(final ListView listView, final HashMap<String,String> criteres, final DBController controller, final Context context)
    {
        Toast.makeText(context, "Requête...", Toast.LENGTH_SHORT).show();

        // === Construction de l'URL de requête ===

        String url = base_url + "?func=rechSalle";

        // Critères :
        for (HashMap.Entry<String,String> entry : criteres.entrySet())
        {
            if (! entry.getValue().equals("null")) {
                url += "&" + entry.getKey() + "=" + entry.getValue();
            }
        }

        Toast.makeText(context, url, Toast.LENGTH_SHORT).show();


        // Init :
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // Lancer la requête :
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response)
            {
                // === Construire un tableau de Strings qui contient toutes les infos des salles (nom, dispo et caractéristiques) ===

                // --- Récupérer les infos du JSON (nom et dispo des salles correspondantes aux critères de recherche) ---

                ArrayList<HashMap<String,String>> liste_salles_json = sallesJSON_to_ArrayList(response);
                if (liste_salles_json.size() != 0)
                {
                    // --- Fusionner les infos des salles de la BDD avec la réponse JSON ---

                    // ArrayList<HashMap<String,String>> liste_salles_fusionee = new ArrayList<HashMap<String,String>>();
                    String[] liste_salles_fusionee = new String[liste_salles_json.size()];

                    for (int i = 0 ; i < liste_salles_json.size() ; i++)
                    {
                        // Retrouver la salle en question dans la BDD :
                        ArrayList<HashMap<String,String>> liste_salle_bdd = controller.getSalles(liste_salles_json.get(i).get("nom"));
                        HashMap<String,String> salle_bdd = liste_salle_bdd.get(0);

                        // Récupérer le nom de la salle :
                        liste_salles_fusionee[i] = liste_salles_json.get(i).get("nom") + ";";

                        // Rajouter les caractéristiques de la BDD dans la String :
                        liste_salles_fusionee[i] += salle_bdd.get("type") + ";";
                        liste_salles_fusionee[i] += salle_bdd.get("projecteur") + ";";

                        int tab = Integer.parseInt(salle_bdd.get("tableau"));
                        liste_salles_fusionee[i] += (tab == 0 || tab == 2) ? "0;" : "1;"; // tableauBlanc
                        liste_salles_fusionee[i] += (tab == 0 || tab == 1) ? "0;" : "1;"; // tableauNoir

                        liste_salles_fusionee[i] += salle_bdd.get("imprimante") + ";";

                        // Et enfin, la disponibilité (depuis le JSON) :
                        liste_salles_fusionee[i] += liste_salles_json.get(i).get("dispo");
                    }

                    // --- Peupler la ListView avec la liste des salles obtenue ---

                    RoomArrayAdapter adapter = new RoomArrayAdapter(
                            context,
                            liste_salles_fusionee
                    );
                    listView.setAdapter(adapter);
                }
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

    public static void dispo(final String Table, final String nom, final String date, final int width, final Context context, final ImageView imageView)
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

        Toast.makeText(context, url, Toast.LENGTH_SHORT).show();

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
