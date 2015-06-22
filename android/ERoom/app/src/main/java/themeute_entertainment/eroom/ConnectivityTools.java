package themeute_entertainment.eroom;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Locale;


public class ConnectivityTools
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private ConnectivityReceiver receiver;
    private SharedPreferences settings;
    private ProgressDialog prgDialog;
    private DBController controller;
    private ContributionDialog contribDialog;
    private final About about;
    private RechSalle rechsalle;
    private final Context context;

    private static final String broadcast_baseUrl = "https://mvx2.esiee.fr/api/broadcast.php?lang=";

    // ====================================================================================
    // == CONSTRUCTEUR
    // ====================================================================================

    public ConnectivityTools(Context context, ProgressDialog prgDialog, ContributionDialog contribDialog, About about, RechSalle rechsalle)
    {
        // Initialisation du Receiver ;
        this.receiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);

        this.context = context;
        this.settings = context.getSharedPreferences("SHARED_PREFS", context.MODE_PRIVATE);
        this.prgDialog = prgDialog;
        this.controller = new DBController(context);
        this.contribDialog = contribDialog;
        this.about = about;
        this.rechsalle = rechsalle;
    }

    public void close(Context context)
    {
        if (this.receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }


    // ====================================================================================
    // == METHODS
    // ====================================================================================

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void checkForBroadcast()
    {
        System.out.println("Broadcast checking...");
        final String lastNew = settings.getString("broadcast_lastNew", "");
        final String lastFlash = settings.getString("broadcast_lastFlash", "");
        final String locale = Locale.getDefault().getLanguage().equals("fr") ? "fr" : "en";

        final boolean newGuy = lastNew.isEmpty();
        final String type = newGuy ? "new" : "flash";

        // Init :
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // Lancer la requête :
        client.post(broadcast_baseUrl+locale+"&type="+type, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response)
            {
                String newFlash, newNew;

                if (newGuy && response != null && !response.isEmpty()) {
                    // Ne devrait apparaître qu'une seule fois :
                    rechsalle.showBroadCastDialog(response, "New");
                } else if (response != null && !response.isEmpty() && !response.equals(lastFlash)) {
                    // Pour les annonces :
                    rechsalle.showBroadCastDialog(response, "Flash");
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


    // ====================================================================================
    // == ConnectivityReceiver
    // ====================================================================================

    public class ConnectivityReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            ConnectivityManager conn = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected())
            {
                // === Connexion ===

                // --- Mise à jour auto de la BDD ---
                if (prgDialog != null) {
                    controller.checkForUpdates(prgDialog);
                }

                // --- Vérification de la connectivité avant d'envoyer une contribution ---
                if (contribDialog != null) {
                    contribDialog.setCondition("internet", true);
                }

                // --- Vérification des broadcast ---
                if (rechsalle != null) {
                    checkForBroadcast();
                }
            }
            else
            {
                // === Pas de connexion ===

                if (contribDialog != null) {
                    contribDialog.setCondition("internet", false);
                }
            }

            // --- Récupération de la version de la BDD ---
            if (about != null) {
                about.printLastDBUpdate();
            }
        }
    }
}