package AlerteServer.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "site")
public class Site {
    @Id
    @Column(name = "dk_code")
    private String dkCode;

    @ManyToOne
    @JoinColumn(name = "departement")
    private Departement departement;

    @ManyToOne
    @JoinColumn(name = "dk_code_parent")
    private Site parent;

    @OneToMany(mappedBy = "parent")
    private Set<Site> subSites = new HashSet<>();

    @OneToMany(mappedBy = "emplacement")
    private Set<Ot> ordresDeTravail = new HashSet<>();

    public String getDkCode() {
        return dkCode;
    }

    public void setDkCode(String dkCode) {
        this.dkCode = dkCode;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Site getParent() {
        return parent;
    }

    public void setParent(Site parent) {
        this.parent = parent;
    }
}