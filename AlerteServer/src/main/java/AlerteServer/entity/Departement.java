package AlerteServer.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departement")
public class Departement {
    @Id
    @Column(name = "num")
    private String num;
    private Double lat;
    @Column(name = "long")
    private Double longitude;

    @OneToMany(mappedBy = "departement")
    private Set<Site> sites = new HashSet<>();

    @OneToMany(mappedBy = "departement")
    private Set<Bulletin> bulletins = new HashSet<>();

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}