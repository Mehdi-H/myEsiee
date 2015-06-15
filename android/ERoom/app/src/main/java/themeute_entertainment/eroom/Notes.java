package themeute_entertainment.eroom;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class Notes extends BaseDrawerActivity
        implements  NavigationDrawerFragment.NavigationDrawerCallbacks,
                    ConnexionDialog.ConnexionDialogListener
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    // Données :
    private SharedPreferences settings;
    private String login, mdp;

    // Android stuff :
    private Context context;


    // ====================================================================================
    // == onCreate()
    // ====================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        this.mNavigationDrawerFragment = super.onCreateDrawer();

        context = getApplicationContext();
        settings = getPreferences(MODE_PRIVATE);

        // ------------------------------------------------------------------------------------
        // -- Récupération du login et mot de passe
        // ------------------------------------------------------------------------------------

        // === Récupérer les identifiants s'ils sont enregistrés ===

        login = settings.getString("login", "");
        mdp = settings.getString("mdp", "");

        // === S'ils ne sont pas enregistrés : pop-up de connexion ===

        if (login.isEmpty() || mdp.isEmpty()) {
            DialogFragment dialog = new ConnexionDialog();
            dialog.show(getSupportFragmentManager(), "ConnexionDialog");
        }


        // ------------------------------------------------------------------------------------
        // -- La fameuse recherche (pour l'année en cours)
        // ------------------------------------------------------------------------------------

        Button currentYear_btn = (Button) findViewById(R.id.currentYear);
        Button archives_btn = (Button) findViewById(R.id.archives);
        final ListView listView = (ListView) findViewById(R.id.notes);

        // === Pour cette année ===

        currentYear_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "Recherche des notes de cette année...", Toast.LENGTH_SHORT).show();
                Aurion.printNotes(listView, context, login, mdp, false);
            }
        });

        // === Dans les archives ===

        archives_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(context, "Recherche des notes dans les archives...", Toast.LENGTH_SHORT).show();
                Aurion.printNotes(listView, context, login, mdp, true);
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
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        // Récupérer les vues de la Dialog :
        Dialog dialogView = dialog.getDialog();
        EditText login_input = (EditText) dialogView.findViewById(R.id.login);
        EditText mdp_input = (EditText) dialogView.findViewById(R.id.mdp);

        // === Récupérer les valeurs du login et mdp ===

        String login = login_input.getText().toString();
        String mdp = mdp_input.getText().toString();

        // === Enregistrement des paramètres ===

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("login", login);
        editor.putString("mdp", mdp);
        editor.apply();

        if (login.isEmpty() || mdp.isEmpty()) {
            Toast.makeText(context, "Vos identifiants Aurion ont été supprimés de l'appareil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Vos identifiants Aurion ont été enregistrés, vous pouvez les modifier dans les options", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }


    // ====================================================================================
    // == Menu
    // ====================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
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
}
