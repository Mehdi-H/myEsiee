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

        // --- Nom de la caractéristique ---
        TextView textView = (TextView) gridView.findViewById(R.id.texte);

        if (caracteristiques[position].startsWith("taille_")) {
            textView.setText(caracteristiques[position].split("_")[1] + " places");
        } else {
            textView.setText(caracteristiques[position]);
        }

        // --- Icône ---
        ImageView imageView = (ImageView) gridView.findViewById(R.id.icone);
        if (caracteristiques[position].startsWith("taille_")) {
            imageView.setImageResource(icones.get("taille"));
        } else {
            imageView.setImageResource(icones.get(caracteristiques[position]));
        }

        return gridView;
    }
}
