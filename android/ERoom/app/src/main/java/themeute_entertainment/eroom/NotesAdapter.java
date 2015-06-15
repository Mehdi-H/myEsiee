package themeute_entertainment.eroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;


public class NotesAdapter extends ArrayAdapter<Note>
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private final Context context;
    private final Note[] notes;
    private HashMap<String,Integer> icones = new HashMap<String,Integer>();


    // ====================================================================================
    // == CONSTRUCTEUR
    // ====================================================================================

    public NotesAdapter(Context context, Note[] notes)
    {
        super(context, -1, notes);
        this.context = context;
        this.notes = notes;
    }


    // ====================================================================================
    // == REMPLISSAGE DU ROW-LAYOUT
    // ====================================================================================

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // === Récupérer le row-layout pour une note ===

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.view_note_entry, parent, false);

        // --- Récupérer les vues du row-layout ---

        TextView note_view = (TextView) rowView.findViewById(R.id.note);
        TextView labelUnite_view = (TextView) rowView.findViewById(R.id.labelUnite);
        TextView codeUnite_view = (TextView) rowView.findViewById(R.id.codeUnite);
        TextView credits_view = (TextView) rowView.findViewById(R.id.credits);

        // === Remplissage de la ligne à partir du tableau de Notes ===

        // Note :
        String note = notes[position].getNote();
        if (note.equals("FX")) {
            note = "Fx";
        }
        note_view.setText(note);
        if (note.equals("F") || note.equals("Fx")) {
            note_view.setTextColor(context.getResources().getColor(R.color.badGrade_red));
        }

        // Crédits :
        String credits = notes[position].getCredit();
        if (credits.startsWith("non")) {
            credits = "Pas de crédit";
        } else if (credits.equals("1")) {
            credits += " crédit";
        } else {
            credits += " crédits";
        }
        credits_view.setText(credits);

        // Reste :
        labelUnite_view.setText(notes[position].getLibelle());
        codeUnite_view.setText(notes[position].getUnite());

        return rowView;
    }
}
