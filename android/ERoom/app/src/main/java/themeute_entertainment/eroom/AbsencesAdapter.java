package themeute_entertainment.eroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;


public class AbsencesAdapter extends ArrayAdapter<Absence>
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private final Context context;
    private final Absence[] absences;
    private HashMap<String,Integer> icones = new HashMap<String,Integer>();


    // ====================================================================================
    // == CONSTRUCTEUR
    // ====================================================================================

    public AbsencesAdapter(Context context, Absence[] absences)
    {
        super(context, -1, absences);
        this.context = context;
        this.absences = absences;
    }


    // ====================================================================================
    // == REMPLISSAGE DU ROW-LAYOUT
    // ====================================================================================

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // === Récupérer le row-layout pour une absence ===

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.view_absence_entry, parent, false);

        // --- Récupérer les vues du row-layout ---

        TextView unite_view = (TextView) rowView.findViewById(R.id.unite);
        TextView infoSeance_view = (TextView) rowView.findViewById(R.id.infoSeance);
        TextView date_view = (TextView) rowView.findViewById(R.id.date);
        TextView motif_view = (TextView) rowView.findViewById(R.id.motif);

        // === Remplissage de la ligne à partir du tableau d'Absences ===

        // --- Ligne 1 : Unité ---
        // Format : "[code] - [unite]"
        unite_view.setText(absences[position].getCode() + " - " + absences[position].getUnite());

        // --- Ligne 2 : Infos séance ---
        // Format : "[activite] avec [intervenant]"
        infoSeance_view.setText(absences[position].getActivite() + " " + context.getResources().getString(R.string.with) + " " + absences[position].getIntervenant());

        // --- Ligne 3, gauche : Date séance ---
        // Format : "Le [date] de [creneau]"
        date_view.setText(context.getResources().getString(R.string.le_date) + " " + absences[position].getDate() + " " + context.getResources().getString(R.string.de_date) + " " + absences[position].getCreneau());

        // --- Ligne 4, droite : Motif ---
        // Format : "[motif]"
        final String motif = absences[position].getMotif();
        motif_view.setText(motif);
        if (! motif.startsWith("Non excus")) {
            motif_view.setTextColor(context.getResources().getColor(R.color.goodGrade_green));
        }

        return rowView;
    }
}
