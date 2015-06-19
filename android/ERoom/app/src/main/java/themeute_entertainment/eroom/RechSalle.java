package themeute_entertainment.eroom;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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

    private ContributionDialog contribDialog;

    // Données :
    private SharedPreferences settings;

    // ADE :
    private ADE ade;

    // Views :
    private ListView listView_salles;
    private TextView noData_textView;
    private AutoCompleteTextView autocomplete_nomSalle;

    // Liste des ToggleButtons de catégories :
    private final ImageButton btn_categ[] = new ImageButton[3];
    private final boolean btn_checked[] = new boolean[3];
    private final String categories[] = new String[] {
            "it",
            "elec",
            "banal"
    };
    private int[] btn_img_accent = new int[] {
            R.drawable.ic_type_it_accent,
            R.drawable.ic_type_elec_accent,
            R.drawable.ic_type_banal_accent
    };
    private int[] btn_img_white = new int[] {
            R.drawable.ic_type_it_white,
            R.drawable.ic_type_elec_white,
            R.drawable.ic_type_banal_white
    };

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
        this.setTitle(R.string.title_activity_rech_salle);


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

        connection = new ConnectivityTools(context, prgDialog, null, null);


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
        btn_categ[0] = (ImageButton) findViewById(R.id.categ_it);
        btn_categ[1] = (ImageButton) findViewById(R.id.categ_elec);
        btn_categ[2] = (ImageButton) findViewById(R.id.categ_banal);

        // Booléens :
        for (int i = 0 ; i < btn_checked.length ; i++) {
            btn_checked[i] = false;
        }

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
        for (int i = 0 ; i < btn_categ.length ; i++) {
            final int index = i;
            btn_categ[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    if (! btn_checked[index])
                    {
                        // Le bouton n'était pas déjà check, on l'active et on désactive les autres :

                        for (int j = 0 ; j < btn_categ.length ; j++) {
                            if (index != j) {
                                // Désactiver les autres boutons :
                                uncheck(j);
                            } else {
                                // Choisir le type de salle :
                                check(j);
                                criteres.put("type", categories[j]);
                                Toast.makeText(context, correspondances.get(criteres.get("type")), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        // Le bouton était déjà check, on le désactive :

                        uncheck(index);
                        criteres.put("type", null);
                        Toast.makeText(context, getResources().getString(R.string.disabled_categ), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
                lancerRechSalle();
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

    public void lancerRechSalle()
    {
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
            criteres.put("occupied", "null_noDisplay");
            ade.rechSalle(listView_salles, noData_textView, criteres);
        }
    }

    public void uncheck(final int index)
    {
        btn_categ[index].setImageResource(btn_img_white[index]);
        btn_checked[index] = false;
    }

    public void check(final int index)
    {
        btn_categ[index].setImageResource(btn_img_accent[index]);
        btn_checked[index] = true;
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

        if (id == R.id.action_update_db) {
            controller.syncSQLiteMySQLDB("manual update", prgDialog, settings, context);
            return true;
        } else if (id == R.id.contribution) {
            contribDialog = new ContributionDialog();
            contribDialog.show(getSupportFragmentManager(), "ContributionDialog");
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
        Spinner spinner_taille = (Spinner) dialogView.findViewById(R.id.taille);
        CheckBox chk_occupied = (CheckBox) dialogView.findViewById(R.id.afficher_occupees);

        // === Construction des critères ===

        int tableau, projecteur, imprimante;

        int epi = spinner_epi.getSelectedItemPosition();
        int etage = spinner_etage.getSelectedItemPosition();
        int taille_pos = spinner_taille.getSelectedItemPosition();

        boolean blanc = chk_tableau_blanc.isChecked(),
                noir = chk_tableau_noir.isChecked(),
                occupied = chk_occupied.isChecked();

        criteres.put("tableau", blanc ? (noir ? "3" : "2" ) : (noir ? "1" : "null"));
        criteres.put("projecteur", chk_projecteur.isChecked() ? "1" : "null");
        criteres.put("imprimante", chk_imprimante.isChecked() ? "1" : "null");

        criteres.put("epi", epi > 0 ? epi-1 + "" : "null");
        criteres.put("etage", etage > 0 ? etage-1 + "" : "null");
        criteres.put("taille", taille_pos > 0 ? spinner_taille.getSelectedItem() + "" : "null");

        criteres.put("occupied", occupied ? "null_display" : "null_noDisplay");

        // Lancement de la requête :
        ade.rechSalle(listView_salles, noData_textView, criteres);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }
}
