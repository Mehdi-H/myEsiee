package themeute_entertainment.eroom;

public class Absence
{
    private String  activite,
                    code,
                    creneau,
                    date,
                    intervenant,
                    motif,
                    nb_heures,
                    unite;

    public Absence() {}

    public String getActivite() {return activite;}
    public String getCode() {return code;}
    public String getCreneau() {return creneau;}
    public String getDate() {return date;}
    public String getIntervenant() {return intervenant;}
    public String getMotif() {return motif;}
    public String getNb_heures() {return nb_heures;}
    public String getUnite() {return unite;}

    public void setActivite(String activite) {this.activite = activite;}
    public void setCode(String code) {this.code = code;}
    public void setCreneau(String creneau) {this.creneau = creneau;}
    public void setDate(String date) {this.date = date;}
    public void setIntervenant(String intervenant) {this.intervenant = intervenant;}
    public void setMotif(String motif) {this.motif = motif;}
    public void setNb_heures(String nb_heures) {this.nb_heures = nb_heures;}
    public void setUnite(String unite) {this.unite = unite;}
}
