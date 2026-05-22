package AlerteServer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "alerte")
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_bulletin")
    @JsonIgnore
    private Bulletin bulletin;

    private Integer type;

    private Integer level;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Bulletin getBulletin() { return bulletin; }
    public void setBulletin(Bulletin bulletin) { this.bulletin = bulletin; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
}