package themeute_entertainment.eroom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class FicheSalle extends ActionBarActivity
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    // BDD SQLite :
    DBController controller = new DBController(this);


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
        String nomSalle = intent.getStringExtra(RechSalle.EXTRA_NOM_SALLE);


        // ------------------------------------------------------------------------------------
        // -- Nom de la salle
        // ------------------------------------------------------------------------------------

        TextView hello = (TextView) findViewById(R.id.nomSalle);
        hello.setText(nomSalle);

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

        ImageView imageET_view = (ImageView) findViewById(R.id.imageET);
        ADE.dispoSalle(nomSalle, "", this, imageET_view);
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
