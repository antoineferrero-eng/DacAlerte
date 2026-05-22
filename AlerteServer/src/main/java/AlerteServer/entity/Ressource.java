package AlerteServer.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ressource")
public class Ressource {
    @Id
    @Column(name = "dk_code")
    private String dkCode;
    private String libFonction;
    private String telPortable;
    private String email;

    @OneToMany(mappedBy = "ressource")
    private Set<Ot> ordresDeTravail = new HashSet<>();

    public String getDkCode() {
        return dkCode;
    }

    public void setDkCode(String dkCode) {
        this.dkCode = dkCode;
    }

    public String getLibFonction() {
        return libFonction;
    }

    public void setLibFonction(String libFonction) {
        this.libFonction = libFonction;
    }

    public String getTelPortable() {
        return telPortable;
    }

    public void setTelPortable(String telPortable) {
        this.telPortable = telPortable;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}