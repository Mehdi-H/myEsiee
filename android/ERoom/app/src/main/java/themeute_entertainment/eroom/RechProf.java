package themeute_entertainment.eroom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class RechProf extends BaseDrawerActivity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private ContributionDialog contribDialog;

    // Données :
    private SharedPreferences settings;
    private String nomProf;
    private int largeur;
    private String date;

    // Views :
    private AutoCompleteTextView autocomplete_nomProf;
    private ImageView imageET_view;

    // Outils :
    private DBController controller;
    private ADE ade;

    // Android stuff :
    private Context context;

    // Date :
    private DatePicker datePicker;
    private Calendar calendar;
    private Button date_btn;
    private int year, month, day;

    // Outils :
    SimpleDateFormat format_API = new SimpleDateFormat("MM/dd/yyyy"); // format américain pour l'API
    SimpleDateFormat format_fr = new SimpleDateFormat("dd/MM/yyyy"); // format normal


    // ====================================================================================
    // == onCreate()
    // ====================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rech_prof);
        this.mNavigationDrawerFragment = super.onCreateDrawer();
        mNavigationDrawerFragment.setCurrentSelectedPosition(2);

        mTitle = getResources().getString(R.string.title_activity_rech_prof);
        this.setTitle(mTitle);

        // Google Analytics :
        Analytics.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Activity")
                .setAction("Visited")
                .setLabel(getTitle()+":onCreate")
                .build());

        context = getApplicationContext();
        settings = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        ade = new ADE(context);
        controller = new DBController(this);

        // ------------------------------------------------------------------------------------
        // -- Initialisation de l'AutoComplete nom prof
        // ------------------------------------------------------------------------------------

        autocomplete_nomProf = (AutoCompleteTextView) findViewById(R.id.nomProf_recherche);

        // Récupération de tous les noms de salles dans la BDD :
        final String[] noms_profs = controller.getNoms("prof");

        // Les utiliser comme adapter pour l'AutoComplete :
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, noms_profs);
        autocomplete_nomProf.setThreshold(1);
        autocomplete_nomProf.setAdapter(adapter);

        // === Valider la recherche à l'appui sur Entrée ===

        autocomplete_nomProf.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            lancerRechProf();
                            return true;
                        default:
                            break;
                    }
                }

                return false;
            }
        });


        // ------------------------------------------------------------------------------------
        // -- Date
        // ------------------------------------------------------------------------------------

        date_btn = (Button) findViewById(R.id.setDate);

        // Date d'aujourd'hui :
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Ecrire la date choisie sur le bouton :
        showDate(format_fr.format(calendar.getTime()));


        // ------------------------------------------------------------------------------------
        // -- La fameuse recherche
        // ------------------------------------------------------------------------------------

        ImageButton searchBtn = (ImageButton) findViewById(R.id.imageButton_search);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                lancerRechProf();
            }
        });
    }

    public void lancerRechProf()
    {
        // Regarder si le champ "Nom prof" est rempli :
        String nomProf_auto = autocomplete_nomProf.getText().toString();
        if (!nomProf_auto.equals(""))
        {
            // Vérifier que le nom existe dans la BDD :
            if (controller.existsIn(nomProf_auto, "prof")) {
                // Aller directement à la fiche salle :
                remplirFicheProf(nomProf_auto);
                mTitle = getResources().getString(R.string.title_activity_rech_prof) + " (" + nomProf_auto + ")";
                RechProf.this.setTitle(mTitle);
            } else {
                Toast.makeText(context, R.string.prof_not_found, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.no_prof_name, Toast.LENGTH_SHORT).show();
        }
    }


    // ====================================================================================
    // == CUSTOM METHODS
    // ====================================================================================

    private void remplirFicheProf(final String nomProf)
    {
        this.nomProf = nomProf;

        // ------------------------------------------------------------------------------------
        // -- Infos
        // ------------------------------------------------------------------------------------

        final GridView infos_view = (GridView) findViewById(R.id.infosProf);
        String infos_string = "";

        // === Récupération des infos dans la BDD ===

        // --- Retrouver le prof en question dans la BDD ---

        ArrayList<HashMap<String,String>> liste_profs_bdd = controller.getProfs(nomProf);
        HashMap<String,String> prof_bdd = liste_profs_bdd.get(0);

        // --- Rajouter les caractéristiques de la BDD dans la String ---

        infos_string += (! prof_bdd.get("bureau").equals("null") ? "bureau_" + prof_bdd.get("bureau") + ";" : "");
        infos_string += (! prof_bdd.get("email").equals("null") ? "email_" + prof_bdd.get("email") : "");

        // === Peuplage de la GridView ===

        System.out.println(infos_string);
        CaracteristiquesAdapter adapter = new CaracteristiquesAdapter(this, infos_string.split(";"));
        infos_view.setAdapter(adapter);


        // ------------------------------------------------------------------------------------
        // -- Ajouter un listener sur la GridView pour lancer client Mail
        // ------------------------------------------------------------------------------------

        infos_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    String email = infos_view.getAdapter().getItem(position).toString();
                    email = email.substring(email.indexOf('_')+1);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{ email }
                    );
                    startActivity(Intent.createChooser(intent, ""));
                }
            }
        });


        // ------------------------------------------------------------------------------------
        // -- Récupérer l'image de l'emploi du temps et l'afficher
        // ------------------------------------------------------------------------------------

        // Largeur de l'écran :
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        largeur = (int) ((double)size.x / 1.7);

        imageET_view = (ImageView) findViewById(R.id.imageET);
        ade.dispo("Prof", nomProf, "", largeur, imageET_view);
    }


    // ====================================================================================
    // == Date Picker
    // ====================================================================================

    public void showDatePickerDialog(View v) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, dateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3)
        {
            // === Afficher la date sur le bouton ===
            // arg1 = year
            // arg2 = month
            // arg3 = day

            calendar.set(arg1, arg2, arg3);
            showDate(format_fr.format(calendar.getTime()));

            // === Générer une nouvelle image pour la nouvelle date ===

            ade.dispo("Prof", nomProf, format_API.format(calendar.getTime()), largeur, imageET_view);
        }
    };

    private void showDate(String date) {
        // Format : "jj/mm/aaaa"
        date_btn.setText(date);
    }


    // ====================================================================================
    // == MENU
    // ====================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rech_prof, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.contribution) {
            contribDialog = new ContributionDialog();
            contribDialog.show(getSupportFragmentManager(), "ContributionDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
