package themeute_entertainment.eroom;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


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

    // ====================================================================================
    // == CONSTRUCTEUR
    // ====================================================================================

    public ConnectivityTools(Context context, ProgressDialog prgDialog, ContributionDialog contribDialog)
    {
        // Initialisation du Receiver ;
        this.receiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);

        this.settings = context.getSharedPreferences("SHARED_PREFS", context.MODE_PRIVATE);
        this.prgDialog = prgDialog;
        this.controller = new DBController(context);
        this.contribDialog = contribDialog;
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

                if (prgDialog != null) {
                    // Toast.makeText(context, "Internet connection OK", Toast.LENGTH_SHORT).show();
                    controller.checkForUpdates(prgDialog);
                }

                if (contribDialog != null) {
                    contribDialog.setCondition("internet", true);
                }
            }
            else
            {
                // === Pas de connexion ===
                // Toast.makeText(context, "No active connection", Toast.LENGTH_SHORT).show();

                if (contribDialog != null) {
                    contribDialog.setCondition("internet", false);
                }
            }
        }
    }
}