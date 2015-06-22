package themeute_entertainment.eroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Text;


public class About extends BaseDrawerActivity
        implements  NavigationDrawerFragment.NavigationDrawerCallbacks
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    private ContributionDialog contribDialog;
    private ConnectivityTools connectivity;

    // Données :
    private SharedPreferences settings;
    private final String getLastUpdate_url = "https://mvx2.esiee.fr/api/bdd.php?func=getLastUpdate";

    // Android stuff :
    private Context context;

    // Views :
    private TextView dateAppli_view, dateDB_view;
    private boolean dateDB_ok = false;

    // private Analytics analytics;


    // ====================================================================================
    // == onCreate()
    // ====================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.mNavigationDrawerFragment = super.onCreateDrawer();
        mNavigationDrawerFragment.setCurrentSelectedPosition(4);

        this.setTitle(R.string.title_activity_about);

        // Google Analytics :
        // analytics = new Analytics();
        Analytics.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Activity")
                .setAction("Visited")
                .setLabel(getTitle()+":onCreate")
                .build());

        context = getApplicationContext();
        settings = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        connectivity = new ConnectivityTools(context, null, null, this, null);

        // ------------------------------------------------------------------------------------
        // -- Vues
        // ------------------------------------------------------------------------------------

        TextView about_view = (TextView) findViewById(R.id.about_text);
        dateAppli_view = (TextView) findViewById(R.id.dateAppli_value);
        dateDB_view = (TextView) findViewById(R.id.dateDB_value);

        // Parser le HTML pour que le lien soit cliquable :
        about_view.setMovementMethod(LinkMovementMethod.getInstance());
        about_view.setText(Html.fromHtml(getResources().getString(R.string.about_text)));

        // ------------------------------------------------------------------------------------
        // -- Récupération et affichage des bonnes dates
        // ------------------------------------------------------------------------------------

        // Version de l'appli :

        String version = "none";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = "debug";
        }
        dateAppli_view.setText(version);

        // Version de la BDD :

        printLastDBUpdate();
    }


    // ====================================================================================
    // == CUSTOM METHODS
    // ====================================================================================

    public void printLastDBUpdate()
    {
        if (!dateDB_ok && !ConnectivityTools.isNetworkAvailable(context)) {
            dateDB_view.setText(R.string.no_connection_warning_about);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(getLastUpdate_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                dateDB_view.setText(response);
                dateDB_ok = true;
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
    // == Menu
    // ====================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.contribution) {
            contribDialog = new ContributionDialog();
            contribDialog.show(getSupportFragmentManager(), "ContributionDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
