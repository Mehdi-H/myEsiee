package themeute_entertainment.eroom;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;


public class ConnexionDialog extends DialogFragment
{
    // ====================================================================================
    // == Interface entre l'activité hôte et le Dialog Fragment
    // ====================================================================================

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ConnexionDialogListener
    {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ConnexionDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the ConnexionDialogListener
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConnexionDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    // ====================================================================================
    // == onCreateDialog()
    // ====================================================================================

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        // === Utiliser le layout custom ===

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_connexion, null));


        // === Boutons "Annuler" et "Connexion" ===

        // Add the buttons
        builder.setPositiveButton(R.string.connexion, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(ConnexionDialog.this);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                // Send the negative button event back to the host activity
                mListener.onDialogNegativeClick(ConnexionDialog.this);
            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }
}
