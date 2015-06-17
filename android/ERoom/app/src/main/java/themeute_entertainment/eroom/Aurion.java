package themeute_entertainment.eroom;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Aurion
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private final String base_url = "https://mvx2.esiee.fr/api/aurion.php";
    private boolean listView_isOnScreen;
    private Context context;
    private SharedPreferences settings;

    // ====================================================================================
    // == CONSTRUCTEUR
    // ====================================================================================

    public Aurion(Context context)
    {
        listView_isOnScreen = false;

        this.context = context;
        settings = context.getSharedPreferences("SHARED_PREFS", context.MODE_PRIVATE);
    }

    // ====================================================================================
    // == METHODES
    // ====================================================================================

    // ------------------------------------------------------------------------------------
    // -- Sauvegardes
    // ------------------------------------------------------------------------------------

    public void loadLastData(final String func, ListView listView, TextView noData_textView)
    {
        String lastData = settings.getString(func+"_lastData", "");
        if (!lastData.equals("")) {
            Toast.makeText(context, "Dernières données affichées", Toast.LENGTH_SHORT).show();
            processJSON(lastData, func, listView, noData_textView);
        }
    }

    // ------------------------------------------------------------------------------------
    // -- Affichages
    // ------------------------------------------------------------------------------------

    private void peuplerListView(final Object[] data, final ArrayAdapter adapter, final ListView listView, final TextView noData_textView)
    {
        if (data.length < 1) {
            if (listView_isOnScreen) {
                ViewGroupUtils.replaceView(listView, noData_textView);
            }
            noData_textView.setText(context.getResources().getString(R.string.noData));
            listView_isOnScreen = false;
        } else {
            ViewGroupUtils.replaceView(noData_textView, listView);
            listView.setAdapter(adapter);
            listView_isOnScreen = true;
        }
    }


    // ------------------------------------------------------------------------------------
    // -- Traitements
    // ------------------------------------------------------------------------------------

    public void processJSON(final String json, final String func, ListView listView, TextView noData_textView)
    {
        // === Récupérer les données dans un tableau d'objets ===

        Gson gson = new Gson();
        System.out.println(json);
        if (func.equals("grades"))
        {
            Note[] data = gson.fromJson(json, Note[].class);
            peuplerListView(data, new NotesAdapter(context, data), listView, noData_textView);

        }
        else if (func.equals("absences"))
        {
            Absence[] data = gson.fromJson(json, Absence[].class);
            peuplerListView(data, new AbsencesAdapter(context, data), listView, noData_textView);
        }
        else
        {
            // Appreciation[] data = gson.fromJson(response, Appreciation[].class);
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(func+"_lastData", json);
        editor.apply();
    }


    // ------------------------------------------------------------------------------------
    // -- Requêtes
    // ------------------------------------------------------------------------------------

    public void request(final String func, final ListView listView, final TextView noData_textView, final String login, final String mdp, final boolean archives)
    {
        if (listView_isOnScreen) {
            ViewGroupUtils.replaceView(listView, noData_textView);
            listView_isOnScreen = false;
        }
        noData_textView.setText(context.getResources().getString(R.string.loading));

        // === Exécuter la requête HTTP POST ===

        // Init :
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.setTimeout(60000);

        // Paramètres :
        params.put("func", archives ? "old_" + func : func);
        params.put("login", login);
        params.put("pwd", mdp);

        // Lancer la requête :
        client.post(base_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                processJSON(response, func, listView, noData_textView);
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


    // ------------------------------------------------------------------------------------
    // -- Interface Custom Dialog Fragment (Connexion)
    // ------------------------------------------------------------------------------------

    public void onDialogPositiveClick(DialogFragment dialog, final String func)
    {
        // Récupérer les vues de la Dialog :
        Dialog dialogView = dialog.getDialog();
        EditText login_input = (EditText) dialogView.findViewById(R.id.login);
        EditText mdp_input = (EditText) dialogView.findViewById(R.id.mdp);

        // === Récupérer les valeurs du login et mdp ===

        String login = login_input.getText().toString();
        String mdp = mdp_input.getText().toString();

        // === Enregistrement des paramètres ===

        SharedPreferences.Editor editor = settings.edit();

        if (login.isEmpty() || mdp.isEmpty()) {
            Toast.makeText(context, "Vos identifiants Aurion ont été supprimés de l'appareil", Toast.LENGTH_SHORT).show();
            editor.putString("login", "");
            editor.putString("mdp", "");
            editor.putString(func+"_lastData", "");
        } else {
            Toast.makeText(context, "Vos identifiants Aurion ont été enregistrés, vous pouvez les modifier dans les options", Toast.LENGTH_LONG).show();
            editor.putString("login", login);
            editor.putString("mdp", mdp);
        }

        editor.apply();
    }

    public void onDialogNegativeClick(DialogFragment dialog) {}
}