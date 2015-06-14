package themeute_entertainment.eroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        icones.put("Projecteur", R.drawable.ic_action_computer);
        icones.put("Imprimante", R.drawable.ic_action_computer);
        icones.put("Tableau blanc", R.drawable.ic_action_computer);
        icones.put("Tableau noir", R.drawable.ic_action_computer);
        icones.put("Salle info", R.drawable.ic_action_computer);
        icones.put("Salle d'élec", R.drawable.ic_action_computer);
        icones.put("Salle de cours", R.drawable.ic_action_computer);
        icones.put("taille", R.drawable.ic_action_computer);
        // Infos prof :
        icones.put("bureau", R.drawable.ic_action_computer);
        icones.put("email", R.drawable.ic_action_computer);
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

        System.out.println(position + " " + caracteristiques[position]);

        if (caracteristiques[position].indexOf("_") != -1)
        {
            String nomCaract = caracteristiques[position].split("_")[0];
            String valCaract = caracteristiques[position].split("_")[1];

            // Cas particuliers de la forme "préfixe_valeur" :
            textView.setText(valCaract + (nomCaract.equals("taille") ? " places" : ""));
            imageView.setImageResource(icones.get(nomCaract));

        } else {
            textView.setText(caracteristiques[position]);
            imageView.setImageResource(icones.get(caracteristiques[position]));
        }

        return gridView;
    }
}
