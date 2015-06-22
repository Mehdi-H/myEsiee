package themeute_entertainment.eroom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Locale;


public class ContributionDialog extends DialogFragment
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private Button positiveButton;
    private boolean type_ok = false,
                    contenu_ok = false,
                    internet_ok = false;
    private ConnectivityTools connectivity;

    private Spinner type_view;
    private EditText email_view, contenu_view;

    private Context context;

    // ====================================================================================
    // == onCreateDialog()
    // ====================================================================================

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        context = getActivity();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // ------------------------------------------------------------------------------------
        // -- Utiliser le layout custom
        // ------------------------------------------------------------------------------------

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_contribution, null);
        builder.setView(view);

        // ------------------------------------------------------------------------------------
        // -- Views
        // ------------------------------------------------------------------------------------

        type_view = (Spinner) view.findViewById(R.id.type);
        email_view = (EditText) view.findViewById(R.id.email);
        contenu_view = (EditText) view.findViewById(R.id.contenu);

        // === Préremplir l'adresse e-mail si elle est en mémoire ===

        final SharedPreferences settings = context.getSharedPreferences("SHARED_PREFS", context.MODE_PRIVATE);

        final String pre_email = settings.getString("email", null);
        if (pre_email != null) {
            email_view.setText(pre_email);
        }

        // ------------------------------------------------------------------------------------
        // -- Boutons Positive et Negative
        // ------------------------------------------------------------------------------------

        // Add the buttons
        builder.setPositiveButton(R.string.contribuer, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                // === Récupérer des infos sur le contexte ===

                final String login = settings.getString("login", "#none");
                final String version_android = "API " + android.os.Build.VERSION.SDK_INT + " (Android " + android.os.Build.VERSION.RELEASE + ")";
                final String location = getActivity().getTitle().toString() + " (LANG : " + Locale.getDefault().getDisplayLanguage() + ")";

                // === Récupérer les infos du formulaire ===

                final String type = type_view.getSelectedItem().toString();
                final String email = email_view.getText().toString();
                final String contenu = contenu_view.getText().toString();

                // === Se souvenir de l'adresse e-mail pour le futur ===

                if (!email.isEmpty()) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("email", email);
                    editor.apply();
                }

                // === Envoyer la contribution ===

                // Init :
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();

                // Paramètres :
                params.put("login", login);
                params.put("type_contrib", type);
                params.put("version_android", version_android);
                params.put("email", email);
                params.put("location", location);
                params.put("contenu", contenu);

                // Lancer la requête :
                client.post("https://mvx2.esiee.fr/api/contribution.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(context, R.string.contrib_sent, Toast.LENGTH_LONG).show();
                    }

                    // When error occurred
                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        if (statusCode == 404) {
                            System.out.println(statusCode + " - Requested resource not found : " + content + "\n" + error.toString());
                        } else if (statusCode == 500) {
                            System.out.println(statusCode + " - Something went wrong at server end : " + content + "\n" + error.toString());
                        } else {
                            System.out.println(statusCode + " - Unexpected Error occurred ! : " + content + "\n" + error.toString());
                        }
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, "=(", Toast.LENGTH_SHORT).show();
            }
        });

        // Afficher la Dialog :
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // ------------------------------------------------------------------------------------
        // -- Désactiver le bouton Positive par défaut
        // ------------------------------------------------------------------------------------

        positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        // ------------------------------------------------------------------------------------
        // -- Listener du Spinner du type pour activer le bouton Positive
        // ------------------------------------------------------------------------------------

        type_view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCondition("type", position > 0);
                ((TextView) parent.getChildAt(0)).setTextColor(
                        position > 0 ? getResources().getColor(R.color.primary_text)
                                : getResources().getColor(R.color.windowBackground)
                );
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ------------------------------------------------------------------------------------
        // -- Listener du champ Contenu pour activer le bouton Positive
        // ------------------------------------------------------------------------------------

        contenu_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setCondition("contenu", contenu_view.getText().length() > 0);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // ------------------------------------------------------------------------------------
        // -- Vérifier connexion internet pour le bouton Positive
        // ------------------------------------------------------------------------------------

        setCondition("internet", ConnectivityTools.isNetworkAvailable(context));
        if (! internet_ok) {
            Toast.makeText(context, R.string.no_connection_warning_contrib, Toast.LENGTH_LONG).show();
        }
        connectivity = new ConnectivityTools(context, null, this, null, null);


        alertDialog.setCanceledOnTouchOutside(false);


        // Create the AlertDialog object and return it
        return alertDialog;
    }


    // ====================================================================================
    // == Modifieurs
    // ====================================================================================

    public void checkIfAllOk()
    {
        // Vérifier si on doit activer le bouton Positive :
        if (type_ok && contenu_ok && internet_ok) {
            positiveButton.setEnabled(true);
        } else {
            positiveButton.setEnabled(false);
        }
    }

    public void setCondition(final String name, final boolean value)
    {
        // Mettre à jour les conditions :
        if (name.equals("type")) {
            this.type_ok = value;
        } else if (name.equals("contenu")){
            this.contenu_ok = value;
        } else if (name.equals("internet")) {
            this.internet_ok = value;
        }

        checkIfAllOk();
    }
}
