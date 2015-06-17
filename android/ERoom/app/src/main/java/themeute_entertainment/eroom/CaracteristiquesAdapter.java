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
        icones.put(context.getResources().getString(R.string.projecteur), R.drawable.ic_caract_projecteur);
        icones.put(context.getResources().getString(R.string.imprimante), R.drawable.ic_caract_imprimante);
        icones.put(context.getResources().getString(R.string.tableau_blanc), R.drawable.ic_caract_tableau_blanc);
        icones.put(context.getResources().getString(R.string.tableau_noir), R.drawable.ic_caract_tableau_noir);
        icones.put(context.getResources().getString(R.string.salle_it), R.drawable.ic_type_it);
        icones.put(context.getResources().getString(R.string.salle_elec), R.drawable.ic_type_elec);
        icones.put(context.getResources().getString(R.string.salle_banal), R.drawable.ic_type_banal);
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
                    textView.setText(R.string.room_s);
                    iconeTaille.setText("S");
                } else if (taille < 70) {
                    textView.setText(R.string.room_m);
                    iconeTaille.setText("M");
                } else {
                    textView.setText(R.string.room_l);
                    iconeTaille.setText("L");
                }
            }
            else
            {
                ViewGroupUtils.removeView(iconeTaille);
                textView.setText(valCaract);
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
