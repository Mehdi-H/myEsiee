package themeute_entertainment.eroom;


import android.content.Context;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

public class Aurion
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private static final String base_url = "https://mvx2.esiee.fr/api/aurion.php";

    // ====================================================================================
    // == METHODES PUBLIQUES
    // ====================================================================================

    public static void printNotes(final ListView listView, final Context context, final String login, final String mdp, final boolean archives)
    {
        // === Exécuter la requête HTTP POST ===

        // Init :
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.setTimeout(60000);

        // Paramètres :
        params.put("func", archives ? "old_grades" : "grades");
        params.put("login", login);
        params.put("pwd", mdp);

        // Lancer la requête :
        client.post(base_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response)
            {
                // === Récupérer les données dans un tableau d'objets Notes ===

                Gson gson = new Gson();
                System.out.println(response);
                Note[] notes = gson.fromJson(response, Note[].class);

                // === Peupler la ListView avec les notes ===

                listView.setAdapter(new NotesAdapter(context, notes));
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

    /**
     * Retourne
     * @param json
     */
    private static String[] getNotesStringArrayFromJSON(String json)
    {
        Gson gson = new GsonBuilder().create();
        String[] notes_stringArray = new String[1];



        return notes_stringArray;
    }
}
