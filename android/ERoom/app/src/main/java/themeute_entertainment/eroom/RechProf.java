package themeute_entertainment.eroom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class RechProf extends BaseDrawerActivity
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    // Données :
    private SharedPreferences settings;
    private String nomProf;
    private int largeur;
    private String date;

    // Views :
    private AutoCompleteTextView autocomplete_nomSalle;
    private ImageView imageET_view;

    // BDD SQLite :
    DBController controller = new DBController(this);

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

        context = getApplicationContext();
        settings = getPreferences(MODE_PRIVATE);

        // ------------------------------------------------------------------------------------
        // -- Initialisation de l'AutoComplete nom prof
        // ------------------------------------------------------------------------------------

        autocomplete_nomSalle = (AutoCompleteTextView) findViewById(R.id.nomProf_recherche);

        // Récupération de tous les noms de salles dans la BDD :
        final String[] noms_profs = controller.getNoms("prof");

        // Les utiliser comme adapter pour l'AutoComplete :
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, noms_profs);
        autocomplete_nomSalle.setThreshold(1);
        autocomplete_nomSalle.setAdapter(adapter);


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
                // Regarder si le champ "Nom prof" est rempli :
                String nomProf_auto = autocomplete_nomSalle.getText().toString();
                if (!nomProf_auto.equals(""))
                {
                    // Vérifier que le nom existe dans la BDD :
                    if (controller.existsIn(nomProf_auto, "prof")) {
                        // Aller directement à la fiche salle :
                        remplirFicheProf(nomProf_auto);
                    } else {
                        Toast.makeText(context, "Prof introuvable =(", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Quel prof rechercher ?", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        GridView infos_view = (GridView) findViewById(R.id.infosProf);
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
        infos_view.setAdapter(new CaracteristiquesAdapter(this, infos_string.split(";")));


        // ------------------------------------------------------------------------------------
        // -- Récupérer l'image de l'emploi du temps et l'afficher
        // ------------------------------------------------------------------------------------

        // Largeur de l'écran :
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        largeur = (int) ((double)size.x / 1.7);

        imageET_view = (ImageView) findViewById(R.id.imageET);
        ADE.dispo("Prof", nomProf, "", largeur, this, imageET_view);
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

            ADE.dispo("Prof", nomProf, format_API.format(calendar.getTime()), largeur, RechProf.this, imageET_view);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}