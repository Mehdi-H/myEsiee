package themeute_entertainment.eroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;


public class CaracteristiquesAdapter extends ArrayAdapter<String>
{

    // ====================================================================================
    // == ATTRIBUTS
    // ====================================================================================

    private final Context context;
    private final String[] caracteristiques;
    private HashMap<String,Integer> icones = new HashMap<String,Integer>();


    // ====================================================================================
    // == CONSTRUCTEUR
    // ====================================================================================

    public CaracteristiquesAdapter(Context context, String[] caracteristiques)
    {
        super(context, -1, caracteristiques);
        this.context = context;
        this.caracteristiques = caracteristiques;

        // Remplissage de la HashMap d'icônes :
        icones.put("Projecteur", R.drawable.ic_caract_projecteur);
        icones.put("Imprimante", R.drawable.ic_caract_imprimante);
        icones.put("Tableau blanc", R.drawable.ic_caract_tableau_blanc);
        icones.put("Tableau noir", R.drawable.ic_caract_tableau_noir);
        icones.put("Salle info", R.drawable.ic_type_it);
        icones.put("Salle d'élec", R.drawable.ic_type_elec);
        icones.put("Salle de cours", R.drawable.ic_type_banal);
        // Infos prof :
        icones.put("bureau", R.drawable.ic_info_bureau);
        icones.put("email", R.drawable.ic_info_email);
    }


    // ====================================================================================
    // == OVERRIDES
    // ====================================================================================

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // === Utiliser le layout custom ===

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView = inflater.inflate(R.layout.view_caracteristiques_entry, parent, false);

        // === Remplir la case courante ===

        TextView textView = (TextView) gridView.findViewById(R.id.texte);
        ImageView imageView = (ImageView) gridView.findViewById(R.id.icone);
        TextView iconeTaille = (TextView) gridView.findViewById(R.id.icone_taille);

        System.out.println(position + " " + caracteristiques[position]);

        if (caracteristiques[position].indexOf("_") != -1)
        {
            String nomCaract = caracteristiques[position].split("_")[0];
            String valCaract = caracteristiques[position].split("_")[1];

            // Cas particuliers de la forme "préfixe_valeur" :
            if (nomCaract.equals("taille"))
            {
                ViewGroupUtils.removeView(imageView);
                int taille = Integer.parseInt(valCaract);
                if (0 < taille && taille < 30) {
                    textView.setText("Petite salle");
                    iconeTaille.setText("S");
                } else if (taille < 70) {
                    textView.setText("Salle moyenne");
                    iconeTaille.setText("M");
                } else {
                    textView.setText("Grande salle");
                    iconeTaille.setText("L");
                }
            }
            else
            {
                ViewGroupUtils.removeView(iconeTaille);
                textView.setText(valCaract + (nomCaract.equals("taille") ? " places" : ""));
                imageView.setImageResource(icones.get(nomCaract));
            }

        } else {
            ViewGroupUtils.removeView(iconeTaille);
            textView.setText(caracteristiques[position]);
            imageView.setImageResource(icones.get(caracteristiques[position]));
        }

        return gridView;
    }
}
