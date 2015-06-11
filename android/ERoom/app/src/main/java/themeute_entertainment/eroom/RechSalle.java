package themeute_entertainment.eroom;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;


public class RechSalle extends ActionBarActivity
        implements  NavigationDrawerFragment.NavigationDrawerCallbacks,
                    AdvancedSearchDialog.AdvancedSearchDialogListener
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // Données :
    private final String base_url = "https://mvx2.esiee.fr/api/ade.php";

    // ListView des salles :
    private ListView listView_salles;

    // Liste des ToggleButtons de catégories :
    private final ToggleButton btn_categ[] = new ToggleButton[3];
    private final String categories[] = new String[3];

    private final TextView[] textView_debug = new TextView[1];

    // Critères de recherche :
    private HashMap<String,String> criteres = new HashMap<String,String>();

    // BDD SQLite :
    DBController controller = new DBController(this);

    // Progress Dialog Object
    ProgressDialog prgDialog;
    HashMap<String,String> queryValues;

    // Android stuff :
    private Context context;

    // ====================================================================================
    // == onCreate()
    // ====================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rech_salle);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        context = getApplicationContext();

        // ------------------------------------------------------------------------------------
        // -- VIEWS
        // ------------------------------------------------------------------------------------

        textView_debug[0] = (TextView) findViewById(R.id.textView_debug);

        final ImageButton searchBtn = (ImageButton) findViewById(R.id.imageButton_search);
        final Button advancedSearch = (Button) findViewById(R.id.btn_advancedSearch);

        // ToggleButtons :
        btn_categ[0] = (ToggleButton) findViewById(R.id.categ_it);
        btn_categ[1] = (ToggleButton) findViewById(R.id.categ_elec);
        btn_categ[2] = (ToggleButton) findViewById(R.id.categ_banal);

        // Catégories associées :
        categories[0] = "it";
        categories[1] = "elec";
        categories[2] = "banal";

        listView_salles = (ListView) findViewById(R.id.listView_salles);


        // ------------------------------------------------------------------------------------
        // -- Initialisation des critères de recherche
        // ------------------------------------------------------------------------------------



        // ------------------------------------------------------------------------------------
        // -- Comportement des ToggleButton
        // ------------------------------------------------------------------------------------

        /**
         * Lorsqu'un bouton de catégorie est coché, décoche tous les autres.
         * Source : http://stackoverflow.com/questions/6282702/how-to-allow-only-1-android-toggle-button-out-of-3-to-be-on-at-once
         */
        CompoundButton.OnCheckedChangeListener changeChecker = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    for (int i = 0 ; i < btn_categ.length ; i++) {
                        // Désactiver les autres boutons :
                        if (buttonView != btn_categ[i]) {
                            btn_categ[i].setChecked(false);
                        } else {
                            // Choisir le type de salle :
                            criteres.put("type", categories[i]);
                            Toast.makeText(context, "Catégorie : " + criteres.get("type"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                // Parcourir les boutons pour voir si l'un d'eux est actif :
                boolean checked = false;
                for (int i = 0 ; i < btn_categ.length ; i++) {
                    if (btn_categ[i].isChecked()) {
                        checked = true;
                    }
                }
                // Désactiver le critère de type si aucun bouton n'est actif :
                if (!checked) {
                    Toast.makeText(context, "Catégorie désactivée", Toast.LENGTH_SHORT).show();
                    criteres.put("type", null);
                }
            }
        };

        for (ToggleButton btn : btn_categ)
        {
            // === Utiliser des icônes ===

            // --- Etat Off ---
            ImageSpan imageSpan = new ImageSpan(this, R.drawable.ic_action_computer);
            SpannableString content = new SpannableString("X");
            content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            btn.setTextOff(content);

            // --- Etat On ---
            ImageSpan imageSpan2 = new ImageSpan(this, R.drawable.ic_action_computer_accent);
            SpannableString content2 = new SpannableString("X");
            content2.setSpan(imageSpan2, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            btn.setTextOn(content2);

            // --- Initialisation ---
            btn.setText(content);

            // === Ne permettre l'état On qu'à un seul bouton ===

            btn.setOnCheckedChangeListener(changeChecker);
        }

        // ------------------------------------------------------------------------------------
        // -- Bouton recherche avancée ==> Alert Dialog
        // ------------------------------------------------------------------------------------

        final DialogFragment dialog = new AdvancedSearchDialog();

        advancedSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Toast.makeText(context, "Recherche avancée", Toast.LENGTH_SHORT).show();
                dialog.show(getSupportFragmentManager(), "AdvancedSearchDialog");
            }
        });

        // ------------------------------------------------------------------------------------
        // -- Requête HTTP Get
        // ------------------------------------------------------------------------------------

        textView_debug[0].setText("Blablabla");

        // Au clic sur le bouton recherche :
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                rechSalle();
            }
        });

        // Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");
        prgDialog.setCancelable(false);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_Rooms);
                break;
            case 2:
                mTitle = getString(R.string.title_Teachers);
                break;
            case 3:
                mTitle = getString(R.string.title_Grades);
                break;
            case 4:
                mTitle = getString(R.string.title_Absences);
                break;
            case 5:
                mTitle = getString(R.string.title_Assessments);
                break;
            case 6:
                mTitle = getString(R.string.title_Settings);
                break;
            case 7:
                mTitle = getString(R.string.title_Disconnect);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.rech_salle, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_update_db) {
            syncSQLiteMySQLDB();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_rech_salle, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((RechSalle) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    // ====================================================================================
    // == CUSTOM METHODS
    // ====================================================================================

    // ------------------------------------------------------------------------------------
    // -- Recherche Salle
    // ------------------------------------------------------------------------------------

    public void rechSalle()
    {
        Toast.makeText(context, "Requête...", Toast.LENGTH_SHORT).show();

        // === Construction de l'URL de requête ===

        String url = base_url + "?func=rechSalle";

        // Critères :
        for (HashMap.Entry<String,String> entry : criteres.entrySet())
        {
            if (! entry.getValue().equals("0")) {
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
                textView_debug[0].setText("Response : " + response);

                // === Construire un tableau de Strings qui contient toutes les infos des salles (nom, dispo et caractéristiques) ===

                // --- Récupérer les infos du JSON (nom et dispo des salles correspondantes aux critères de recherche) ---

                ArrayList<HashMap<String,String>> liste_salles_json = jsonToArrayList(response);
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
                            RechSalle.this,
                            liste_salles_fusionee
                    );
                    listView_salles.setAdapter(adapter);
                }
            }

            // When error occurred
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    textView_debug[0].setText(statusCode + " - Requested resource not found : " + content + "\n" + error.toString());
                } else if (statusCode == 500) {
                    textView_debug[0].setText(statusCode + " - Something went wrong at server end : " + content + "\n" + error.toString());
                } else {
                    textView_debug[0].setText(statusCode + " - Unexpected Error occurred ! : " + content + "\n" + error.toString());
                }
            }
        });
    }


    // ------------------------------------------------------------------------------------
    // -- Interface custom Dialog Fragment
    // ------------------------------------------------------------------------------------

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface

    @Override
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        // Récupérer les vues de la Dialog :
        Dialog dialogView = dialog.getDialog();
        Spinner spinner_epi = (Spinner) dialogView.findViewById(R.id.epi);
        Spinner spinner_etage = (Spinner) dialogView.findViewById(R.id.etage);
        CheckBox chk_tableau_blanc = (CheckBox) dialogView.findViewById(R.id.tableau_blanc);
        CheckBox chk_tableau_noir = (CheckBox) dialogView.findViewById(R.id.tableau_noir);
        CheckBox chk_projecteur = (CheckBox) dialogView.findViewById(R.id.projecteur);
        CheckBox chk_imprimante = (CheckBox) dialogView.findViewById(R.id.imprimante);

        // === Construction des critères ===

        int epi, etage, tableau, projecteur, imprimante;

        boolean blanc = chk_tableau_blanc.isChecked(),
                noir = chk_tableau_noir.isChecked();

        criteres.put("tableau", blanc ? (noir ? "3" : "2" ) : (noir ? "1" : "0"));
        criteres.put("projecteur", chk_projecteur.isChecked() ? "1" : "0");
        criteres.put("imprimante", chk_imprimante.isChecked() ? "1" : "0");

        // Lancement de la requête :
        rechSalle();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }


    // ------------------------------------------------------------------------------------
    // -- JSON
    // ------------------------------------------------------------------------------------

    /**
     *
     * @param json
     */
    public ArrayList<HashMap<String,String>> jsonToArrayList(String json)
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

    // ------------------------------------------------------------------------------------
    // -- Synchronisation MySQL serveur -> SQLite Android
    // ------------------------------------------------------------------------------------

    /**
     * Method to Sync MySQL to SQLite DB
     */
    public void syncSQLiteMySQLDB()
    {
        // Create AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();

        // Pour accepter les requêtes HTTPS sans certificat :
        // -- Source : http://stackoverflow.com/a/28222107/2372933
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        }
        catch (Exception e) {}

        // Show ProgressBar
        prgDialog.show();

        // On supprime toute la BDD locale et on la reconstruit :
        controller.onUpgrade(controller.getWritableDatabase(), 0, 1);

        requestData(client, params, "salle");
        requestData(client, params, "prof");
    }

    public void requestData(AsyncHttpClient client, RequestParams params, final String table)
    {
        client.post("https://mvx2.esiee.fr/mysql_sync/getdata.php?table=" + table, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                // Hide ProgressBar
                prgDialog.hide();
                textView_debug[0].append("\nResponse : " + response);
                updateSQLite(response, table);
            }
            // When error occurred
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet] : " + statusCode ,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Met à jour la BDD SQLite Android
     * @param response
     */
    public void updateSQLite(String response, String table)
    {
        ArrayList<HashMap<String,String>> usersynclist = new ArrayList<HashMap<String, String>>();

        // Create GSON object
        Gson gson = new GsonBuilder().create();

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
                    queryValues = new HashMap<String, String>();

                    if (table.equals("salle")) {
                        queryValues.put("nom", obj.get("nom").toString());
                        queryValues.put("resourceID", obj.get("resourceID").toString());
                        queryValues.put("type", obj.get("type").toString());
                        queryValues.put("taille", obj.get("taille").toString());
                        queryValues.put("projecteur", obj.get("projecteur").toString());
                        queryValues.put("tableau", obj.get("tableau").toString());
                        queryValues.put("imprimante", obj.get("imprimante").toString());

                        controller.insertSalle(queryValues);
                    } else if (table.equals("prof")) {
                        queryValues.put("nom", obj.get("nom").toString());
                        queryValues.put("resourceID", obj.get("resourceID").toString());
                        queryValues.put("bureau", obj.get("bureau").toString());
                        queryValues.put("email", obj.get("email").toString());

                        controller.insertProf(queryValues);
                    }

                    HashMap<String,String> map = new HashMap<String,String>();
                }
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), RechSalle.class);
        // startActivity(objIntent);
    }

}
