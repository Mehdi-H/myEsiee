package themeute_entertainment.eroom;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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


public class RechSalle extends BaseDrawerActivity
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
    private SharedPreferences settings;

    // Views :
    private ListView listView_salles;
    private AutoCompleteTextView autocomplete_nomSalle;

    // Liste des ToggleButtons de catégories :
    private final ToggleButton btn_categ[] = new ToggleButton[3];
    private final String categories[] = new String[3];

    // Critères de recherche :
    private HashMap<String,String> criteres = new HashMap<String,String>();

    // BDD SQLite :
    DBController controller = new DBController(this);

    // Progress Dialog Object
    ProgressDialog prgDialog;

    // Android stuff :
    private Context context;

    // Intent :
    public final static String EXTRA_NOM_SALLE = "themeute_entertainment.eroom.NOM_SALLE";


    // ====================================================================================
    // == onCreate()
    // ====================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rech_salle);
        this.mNavigationDrawerFragment = super.onCreateDrawer();

        mTitle = getTitle();

        context = getApplicationContext();
        settings = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);

        // ------------------------------------------------------------------------------------
        // -- Init Progress Dialog
        // ------------------------------------------------------------------------------------

        // Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Mise à jour de la base de donnée...");
        prgDialog.setCancelable(false);


        // ------------------------------------------------------------------------------------
        // -- Vérification de la version de la BDD
        // ------------------------------------------------------------------------------------

        controller.checkForUpdates(prgDialog, settings, context);


        // ------------------------------------------------------------------------------------
        // -- VIEWS
        // ------------------------------------------------------------------------------------

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
        // -- Initialisation de l'AutoComplete nom salle
        // ------------------------------------------------------------------------------------

        autocomplete_nomSalle = (AutoCompleteTextView) findViewById(R.id.nomSalle);

        // Récupération de tous les noms de salles dans la BDD :
        final String[] noms_salles = controller.getNoms("salle");

        // Les utiliser comme adapter pour l'AutoComplete :
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, noms_salles);
        autocomplete_nomSalle.setThreshold(1);
        autocomplete_nomSalle.setAdapter(adapter);


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
                dialog.show(getSupportFragmentManager(), "AdvancedSearchDialog");
            }
        });

        // ------------------------------------------------------------------------------------
        // -- Requête HTTP Get
        // ------------------------------------------------------------------------------------

        // Au clic sur le bouton recherche :
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Regarder si le champ "Nom salle" est rempli :
                String nomSalle_auto = autocomplete_nomSalle.getText().toString();
                if (!nomSalle_auto.equals("")) {
                    // Vérifier que le nom existe dans la BDD :
                    if (controller.existsIn(nomSalle_auto, "salle")) {
                        // Aller directement à la fiche salle :
                        newFicheSalleActivity(nomSalle_auto);
                    } else {
                        Toast.makeText(context, "Salle introuvable =(", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ADE.rechSalle(listView_salles, criteres, controller, context);
                }
            }
        });

        // ------------------------------------------------------------------------------------
        // -- Choix d'une salle
        // ------------------------------------------------------------------------------------

        listView_salles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView nomSalle_view = (TextView) view.findViewById(R.id.nomSalle);
                newFicheSalleActivity(nomSalle_view.getText().toString());
            }
        });
    }

    /* @Override
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
    } */

    /* public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    } */


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
            controller.syncSQLiteMySQLDB("manual update", prgDialog, settings, context);
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

    private void newFicheSalleActivity(String title)
    {
        Intent intent = new Intent(RechSalle.this, FicheSalle.class);
        intent.putExtra(EXTRA_NOM_SALLE, title);
        startActivity(intent);
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

        int tableau, projecteur, imprimante;

        String epi = spinner_epi.getSelectedItem().toString();
        String etage = spinner_etage.getSelectedItem().toString();

        boolean blanc = chk_tableau_blanc.isChecked(),
                noir = chk_tableau_noir.isChecked();

        criteres.put("tableau", blanc ? (noir ? "3" : "2" ) : (noir ? "1" : "null"));
        criteres.put("projecteur", chk_projecteur.isChecked() ? "1" : "null");
        criteres.put("imprimante", chk_imprimante.isChecked() ? "1" : "null");

        criteres.put("epi", epi.equals("Épi") ? "null" : spinner_epi.getSelectedItemPosition()-1 + "");
        criteres.put("etage", etage.equals("Étage") ? "null" : spinner_etage.getSelectedItemPosition()-1 + "");

        // Lancement de la requête :
        ADE.rechSalle(listView_salles, criteres, controller, context);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }




}
