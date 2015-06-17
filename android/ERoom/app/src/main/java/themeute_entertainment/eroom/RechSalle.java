package themeute_entertainment.eroom;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    // ADE :
    private ADE ade;

    // Views :
    private ListView listView_salles;
    private TextView noData_textView;
    private AutoCompleteTextView autocomplete_nomSalle;

    // Liste des ToggleButtons de catégories :
    private final ToggleButton btn_categ[] = new ToggleButton[3];
    private final String categories[] = new String[3];

    // Critères de recherche :
    private HashMap<String,String> criteres = new HashMap<String,String>();

    // BDD SQLite :
    DBController controller;

    private ConnectivityTools connection;

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
        mNavigationDrawerFragment.setCurrentSelectedPosition(0);

        mTitle = getTitle();

        context = getApplicationContext();
        settings = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        ade = new ADE(context);
        controller = new DBController(context);


        // ------------------------------------------------------------------------------------
        // -- Init Progress Dialog
        // ------------------------------------------------------------------------------------

        // Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage(getResources().getString(R.string.db_update));
        prgDialog.setCancelable(false);

        connection = new ConnectivityTools(context, prgDialog);


        // ------------------------------------------------------------------------------------
        // -- Vérification de la version de la BDD
        // ------------------------------------------------------------------------------------

        controller.checkForUpdates(prgDialog);


        // ------------------------------------------------------------------------------------
        // -- VIEWS
        // ------------------------------------------------------------------------------------

        final ImageButton searchBtn = (ImageButton) findViewById(R.id.imageButton_search);
        final ImageButton advancedSearch = (ImageButton) findViewById(R.id.btn_advancedSearch);

        // ToggleButtons :
        btn_categ[0] = (ToggleButton) findViewById(R.id.categ_it);
        btn_categ[1] = (ToggleButton) findViewById(R.id.categ_elec);
        btn_categ[2] = (ToggleButton) findViewById(R.id.categ_banal);

        // Catégories associées :
        categories[0] = "it";
        categories[1] = "elec";
        categories[2] = "banal";

        listView_salles = (ListView) findViewById(R.id.listView_salles);
        noData_textView = (TextView) findViewById(R.id.noData_textView);

        final HashMap<String,String> correspondances = new HashMap<String,String>();
        correspondances.put("it", getResources().getString(R.string.salle_it));
        correspondances.put("elec", getResources().getString(R.string.salle_elec));
        correspondances.put("banal", getResources().getString(R.string.salle_banal));


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
                            Toast.makeText(context, correspondances.get(criteres.get("type")), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, getResources().getString(R.string.disabled_categ), Toast.LENGTH_SHORT).show();
                    criteres.put("type", null);
                }
            }
        };

        for (ToggleButton btn : btn_categ)
        {
            // === Utiliser des icônes ===

            ImageSpan image_off, image_on;
            Drawable iconeOff, iconeOn;

            switch (btn.getId()) {
                case R.id.categ_it :
                    iconeOff = getResources().getDrawable(R.drawable.ic_type_it_white);
                    iconeOn = getResources().getDrawable(R.drawable.ic_type_it_accent);
                    break;
                case R.id.categ_elec :
                    iconeOff = getResources().getDrawable(R.drawable.ic_type_elec_white);
                    iconeOn = getResources().getDrawable(R.drawable.ic_type_elec_accent);
                    break;
                default : // R.id.categ_banal
                    iconeOff = getResources().getDrawable(R.drawable.ic_type_banal_white);
                    iconeOn = getResources().getDrawable(R.drawable.ic_type_banal_accent);
            }

            // === Etat Off ===

            iconeOff.setBounds(0, 0, 60, 60);
            image_off = new ImageSpan(iconeOff);
            SpannableString content_off = new SpannableString("x");
            content_off.setSpan(image_off, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            btn.setTextOff(content_off);

            // === Etat On ===

            iconeOn.setBounds(0, 0, 60, 60);
            image_on = new ImageSpan(iconeOn);
            SpannableString content_on = new SpannableString("x");
            content_on.setSpan(image_on, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            btn.setTextOn(content_on);

            // === Au démarrage ===

            btn.setText(content_off);

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
                        Toast.makeText(context, getResources().getString(R.string.room_not_found), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ade.rechSalle(listView_salles, noData_textView, criteres);
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

        int epi = spinner_epi.getSelectedItemPosition();
        int etage = spinner_etage.getSelectedItemPosition();

        boolean blanc = chk_tableau_blanc.isChecked(),
                noir = chk_tableau_noir.isChecked();

        criteres.put("tableau", blanc ? (noir ? "3" : "2" ) : (noir ? "1" : "null"));
        criteres.put("projecteur", chk_projecteur.isChecked() ? "1" : "null");
        criteres.put("imprimante", chk_imprimante.isChecked() ? "1" : "null");

        criteres.put("epi", epi > 0 ? epi-1 + "" : "null");
        criteres.put("etage", etage > 0 ? etage-1 + "" : "null");

        // Lancement de la requête :
        ade.rechSalle(listView_salles, noData_textView, criteres);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }
}
