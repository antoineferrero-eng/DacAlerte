package AlerteServer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordre_de_travail")
public class Ot {

    @Id
    @Column(name = "numero_ot")
    private String numeroOt;

    @Column(name = "cr_debut_intervention")
    private LocalDateTime crDebutIntervention;

    @ManyToOne
    @JoinColumn(name = "dk_code_ressource")
    private Ressource ressource;

    @ManyToOne
    @JoinColumn(name = "dk_code_emplacement")
    private Site emplacement;

    public String getNumeroOt() {
        return numeroOt;
    }

    public void setNumeroOt(String numeroOt) {
        this.numeroOt = numeroOt;
    }

    public LocalDateTime getCrDebutIntervention() {
        return crDebutIntervention;
    }

    public void setCrDebutIntervention(LocalDateTime crDebutIntervention) {
        this.crDebutIntervention = crDebutIntervention;
    }

    public Ressource getRessource() {
        return ressource;
    }

    public void setRessource(Ressource ressource) {
        this.ressource = ressource;
    }

    public Site getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(Site emplacement) {
        this.emplacement = emplacement;
    }
}