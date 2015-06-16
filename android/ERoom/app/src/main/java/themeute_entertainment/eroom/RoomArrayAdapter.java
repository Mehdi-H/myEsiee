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
        ImageView iconeProjecteur = (ImageView) rowView.findViewById(R.id.icone_projecteur);
        ImageView iconeTableauBlanc = (ImageView) rowView.findViewById(R.id.icone_tableau_blanc);
        ImageView iconeTableauNoir = (ImageView) rowView.findViewById(R.id.icone_tableau_noir);
        ImageView iconeImprimante = (ImageView) rowView.findViewById(R.id.icone_imprimante);
        TextView iconeTaille = (TextView) rowView.findViewById(R.id.icone_taille);
        TextView dispo = (TextView) rowView.findViewById(R.id.dispo);

        // === Découpage de la String décrivant la ligne à remplir ===
        // Format : "nomSalle;type;projecteur;tableauBlanc;tableauNoir;imprimante;taille;dispo"

        String[] rowValues = values[position].split(";");

        // === Remplissage de la ligne à partir du tableau de Strings values ===

        // Nom salle :
         nomSalle.setText(rowValues[0]);

        // Icône du type :
        String type = rowValues[1];
        if (type.equals("it")) {
            iconeType.setImageResource(R.drawable.ic_type_it);
        } else if (type.equals("elec")) {
            iconeType.setImageResource(R.drawable.ic_type_elec);
        } else { // banal
            iconeType.setImageResource(R.drawable.ic_type_banal);
        }

        // --- Icônes des caractéristiques ---

        ImageView[] imageViews = new ImageView[] {
                iconeProjecteur,
                iconeTableauBlanc,
                iconeTableauNoir,
                iconeImprimante
        };
        int[] drawables = new int[] {
                R.drawable.ic_caract_projecteur,
                R.drawable.ic_caract_tableau_blanc,
                R.drawable.ic_caract_tableau_noir,
                R.drawable.ic_caract_imprimante
        };
        for (int i = 0 ; i < imageViews.length ; i++) {
            int j = i + 2;
            if (rowValues[j].equals("1")) {
                imageViews[i].setImageResource(drawables[i]);
            } else {
                ViewGroupUtils.removeView(imageViews[i]); // supprime la vue complètement
                // imageViews[i].setImageDrawable(null); // efface l'image mais garde la place
            }
        }

        // --- La taille ---

        iconeTaille.setText(rowValues[6]);

        // --- Disponibilité ---

        dispo.setText(rowValues[7]);

        return rowView;
    }

}
