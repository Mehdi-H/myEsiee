package themeute_entertainment.eroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashMap;


/**
 * Activité "Appreciations".
 */
public class Appreciations extends BaseDrawerActivity
        implements  NavigationDrawerFragment.NavigationDrawerCallbacks,
        ConnexionDialog.ConnexionDialogListener
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    private Aurion aurion;
    private final String func = "appreciations";

    // Données :
    private SharedPreferences settings;
    private String login, mdp;

    // Android stuff :
    private Context context;

    // Views :
    private ListView listView;
    private TextView noData_textView;
    private Button archives_btn;
    private Button currentYear_btn;


    // ====================================================================================
    // == onCreate()
    // ====================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appreciations);
        this.mNavigationDrawerFragment = super.onCreateDrawer();
        mNavigationDrawerFragment.setCurrentSelectedPosition(4);

        mTitle = "Appréciations";
        context = getApplicationContext();
        settings = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        aurion = new Aurion(context);

        // ------------------------------------------------------------------------------------
        // -- Vues
        // ------------------------------------------------------------------------------------

        currentYear_btn = (Button) findViewById(R.id.currentYear);
        archives_btn = (Button) findViewById(R.id.archives);
        listView = (ListView) findViewById(R.id.listView);
        noData_textView = (TextView) findViewById(R.id.noData_textView);


        // ------------------------------------------------------------------------------------
        // -- Récupération du login et mot de passe
        // ------------------------------------------------------------------------------------

        // === Récupérer les identifiants s'ils sont enregistrés ===

        login = settings.getString("login", "");
        mdp = settings.getString("mdp", "");

        // === S'ils ne sont pas enregistrés : pop-up de connexion ===

        if (login.equals("") || mdp.equals("")) {
            DialogFragment dialog = new ConnexionDialog();
            dialog.show(getSupportFragmentManager(), "ConnexionDialog");
        } else {
            // Réaffichage des dernières données affichées :
            aurion.loadLastData(func, listView, noData_textView);
        }


        // ------------------------------------------------------------------------------------
        // -- La fameuse recherche (pour l'année en cours)
        // ------------------------------------------------------------------------------------

        currentYear_btn.setEnabled(!(mdp.equals("") || login.equals("")));
        archives_btn.setEnabled(!(mdp.equals("") || login.equals("")));

        // === Pour cette année ===

        currentYear_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, R.string.fetching_assesments, Toast.LENGTH_SHORT).show();
                aurion.request(func, listView, noData_textView, login, mdp, false);
            }
        });

        // === Dans les archives ===

        archives_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, R.string.fetching_assesments_old, Toast.LENGTH_SHORT).show();
                aurion.request(func, listView, noData_textView, login, mdp, true);
            }
        });
    }


    // ====================================================================================
    // == Interface Custom Dialog Fragment (Connexion)
    // ====================================================================================

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        aurion.onDialogPositiveClick(dialog, func);
        currentYear_btn.setEnabled(!(mdp.equals("") || login.equals("")));
        archives_btn.setEnabled(!(mdp.equals("") || login.equals("")));
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        aurion.onDialogNegativeClick(dialog);
    }


    // ====================================================================================
    // == Menu
    // ====================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appreciations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_editAurion) {
            DialogFragment dialog = new ConnexionDialog();
            dialog.show(getSupportFragmentManager(), "ConnexionDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Une appréciation (sert à récupérer les données au format JSON).
     */
    public class Appreciation
    {
        private String annee, commentaire, periode;
        public String getAnnee() {return annee;}
        public String getCommentaire() {return commentaire;}
        public String getPeriode() {return periode;}
    }

    /**
     * L'Adapter pour la ListView des appréciations.
     */
    public class AppreciationsAdapter extends ArrayAdapter<Appreciation>
    {
        // ====================================================================================
        // == ATTRIBUTS
        // ====================================================================================

        private final Context context;
        private final Appreciation[] appreciations;
        private HashMap<String,Integer> icones = new HashMap<String,Integer>();


        // ====================================================================================
        // == CONSTRUCTEUR
        // ====================================================================================

        public AppreciationsAdapter(Context context, Appreciation[] appreciations)
        {
            super(context, -1, appreciations);
            this.context = context;
            this.appreciations = appreciations;
        }


        // ====================================================================================
        // == REMPLISSAGE DU ROW-LAYOUT
        // ====================================================================================

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // === Récupérer le row-layout pour une absence ===

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.view_appreciation_entry, parent, false);

            // --- Récupérer les vues du row-layout ---

            TextView commentaire_view = (TextView) rowView.findViewById(R.id.commentaire);
            TextView periode_view = (TextView) rowView.findViewById(R.id.periode);
            TextView annee_view = (TextView) rowView.findViewById(R.id.annee);

            // === Remplissage de la ligne à partir du tableau d'Appreciations ===

            commentaire_view.setText(appreciations[position].getCommentaire());
            periode_view.setText(appreciations[position].getPeriode());
            annee_view.setText(appreciations[position].getAnnee());

            return rowView;
        }
    }
}