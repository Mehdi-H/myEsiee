package themeute_entertainment.eroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Source : http://www.vogella.com/tutorials/AndroidListView/article.html#adapterown_example
 */
public class RoomArrayAdapter extends ArrayAdapter<String>
{
    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private final Context context;
    private final String[] values;

    // ====================================================================================
    // == CONSTRUCTEUR
    // ====================================================================================

    public RoomArrayAdapter(Context context, String[] values)
    {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    // ====================================================================================
    // == REMPLISSAGE DU ROW-LAYOUT
    // ====================================================================================

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // === Récupérer le row-layout pour une salle ===

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.view_salle_entry, parent, false);

        // --- Récupérer les vues du row-layout ---

        TextView nomSalle = (TextView) rowView.findViewById(R.id.nomSalle);
        ImageView iconeType = (ImageView) rowView.findViewById(R.id.icone_type);
        TextView iconeType_debug = (TextView) rowView.findViewById(R.id.icone_type_debug);
        TextView iconeProjecteur = (TextView) rowView.findViewById(R.id.icone_projecteur);
        TextView iconeTableauBlanc = (TextView) rowView.findViewById(R.id.icone_tableau_blanc);
        TextView iconeTableauNoir = (TextView) rowView.findViewById(R.id.icone_tableau_noir);
        TextView iconeImprimante = (TextView) rowView.findViewById(R.id.icone_imprimante);
        TextView dispo = (TextView) rowView.findViewById(R.id.dispo);

        // === Découpage de la String décrivant la ligne à remplir ===
        // Format : "nomSalle;type;projecteur;tableauBlanc;tableauNoir;imprimante"

        String[] rowValues = values[position].split(";");

        // === Remplissage de la ligne à partir du tableau de Strings values ===

        // Nom salle :
        nomSalle.setText(rowValues[0]);

        // Icône du type :
        String type = rowValues[1];
        if (type.equals("IT")) {
            iconeType.setImageResource(R.drawable.ic_action_computer_accent);
        } else if (type.equals("vid")) {
            iconeType.setImageResource(R.drawable.ic_action_search);
        }
        iconeType_debug.setText(rowValues[1]);

        // --- Icônes des caractéristiques ---

        iconeProjecteur.setText(rowValues[2].equals("0") ? "NoProjo" : "Projo");
        iconeTableauBlanc.setText(rowValues[3].equals("0") ? "NoTabloB" : "TabloB");
        iconeTableauNoir.setText(rowValues[4].equals("0") ? "NoTabloN" : "TabloN");
        iconeImprimante.setText(rowValues[5].equals("0") ? "NoPrinto" : "Printo");

        // --- Disponibilité ---

        dispo.setText(rowValues[6]);

        return rowView;
    }


}
