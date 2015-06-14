package themeute_entertainment.eroom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class FicheSalle extends ActionBarActivity
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    // BDD SQLite :
    DBController controller = new DBController(this);

    // Date :
    private DatePicker datePicker;
    private Calendar calendar;
    private Button date_btn;
    private int year, month, day;

    // Views :
    private ImageView imageET_view;

    // Outils :
    SimpleDateFormat format_API = new SimpleDateFormat("MM/dd/yyyy"); // format américain pour l'API
    SimpleDateFormat format_fr = new SimpleDateFormat("dd/MM/yyyy"); // format normal

    // Données :
    private String nomSalle;
    private int largeur;
    String date;


    // ====================================================================================
    // == onCreate()
    // ====================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_salle);

        // Récupérer l'intent qui a démarré l'activité :
        Intent intent = getIntent();
        nomSalle = intent.getStringExtra(RechSalle.EXTRA_NOM_SALLE);


        // ------------------------------------------------------------------------------------
        // -- Views
        // ------------------------------------------------------------------------------------

        date_btn = (Button) findViewById(R.id.setDate);


        // ------------------------------------------------------------------------------------
        // -- Date
        // ------------------------------------------------------------------------------------

        // Date d'aujourd'hui :
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Ecrire la date choisie sur le bouton :
        showDate(format_fr.format(calendar.getTime()));


        // ------------------------------------------------------------------------------------
        // -- Nom de la salle
        // ------------------------------------------------------------------------------------

        setTitle(nomSalle);


        // ------------------------------------------------------------------------------------
        // -- Caractéristiques
        // ------------------------------------------------------------------------------------

        GridView caracteristiques_view = (GridView) findViewById(R.id.caracteristiques);
        String caract_string = "";

        HashMap<String,String> corresp_types = new HashMap<String,String>();
        corresp_types.put("it", "Salle info");
        corresp_types.put("elec", "Salle d'élec");
        corresp_types.put("banal", "Salle de cours");

        // === Récupération des caractéristiques dans la BDD ===

        // --- Retrouver la salle en question dans la BDD ---

        ArrayList<HashMap<String,String>> liste_salle_bdd = controller.getSalles(nomSalle);
        HashMap<String,String> salle_bdd = liste_salle_bdd.get(0);

        // --- Rajouter les caractéristiques de la BDD dans la String ---

        caract_string += corresp_types.get(salle_bdd.get("type")) + ";";
        caract_string += (salle_bdd.get("projecteur").equals("1") ? "Projecteur;" : "");
        caract_string += (salle_bdd.get("imprimante").equals("1") ? "Imprimante;" : "");
        caract_string += (salle_bdd.get("tableau").equals("1") || salle_bdd.get("tableau").equals("3") ? "Tableau blanc;" : "");
        caract_string += (salle_bdd.get("tableau").equals("2") || salle_bdd.get("tableau").equals("3") ? "Tableau noir;" : "");
        caract_string += "taille_" + salle_bdd.get("taille");

        // === Peuplage de la GridView ===

        caracteristiques_view.setAdapter(new CaracteristiquesAdapter(this, caract_string.split(";")));


        // ------------------------------------------------------------------------------------
        // -- Récupérer l'image de l'emploi du temps et l'afficher
        // ------------------------------------------------------------------------------------

        // Largeur de l'écran :
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        largeur = (int) ((double)size.x / 1.7);

        imageET_view = (ImageView) findViewById(R.id.imageET);
        ADE.dispo("Salle", nomSalle, "", largeur, this, imageET_view);
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

            ADE.dispo("Salle", nomSalle, format_API.format(calendar.getTime()), largeur, FicheSalle.this, imageET_view);
        }
    };

    private void showDate(String date) {
        // Format : "jj/mm/aaaa"
        date_btn.setText(date);
    }


    // ====================================================================================
    // == Menu
    // ====================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fiche_salle, menu);
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
