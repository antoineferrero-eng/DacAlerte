package AlerteServer.repository;

import AlerteServer.dto.ContactAlerteDTO;
import AlerteServer.dto.RessourceManageDTO;
import AlerteServer.entity.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RessourceRepository extends JpaRepository<Ressource, String> {

    @Query("SELECT DISTINCT new AlerteServer.dto.ContactAlerteDTO(r.email, r.telPortable, a.type, a.level) " +
            "FROM Ot odt " +
            "JOIN odt.ressource r " +
            "JOIN odt.emplacement s " +
            "JOIN Bulletin b ON b.departement = s.departement " +
            "JOIN b.alertes a " +
            "WHERE b.date = :date AND s.departement.num = :deptNum " +
            "ORDER BY a.level DESC")
    List<ContactAlerteDTO> findContactsByAlerte(@Param("date") LocalDate date, @Param("deptNum") String deptNum);

    @Query("SELECT DISTINCT new AlerteServer.dto.ContactAlerteDTO(r.email, r.telPortable, a.type, a.level) " +
            "FROM Ot odt " +
            "JOIN odt.ressource r " +
            "JOIN odt.emplacement s " +
            "JOIN Bulletin b ON b.departement = s.departement " +
            "JOIN b.alertes a " +
            "WHERE r.dkCode = :dkCode AND b.date = :date " +
            "ORDER BY a.level DESC")
    List<ContactAlerteDTO> findContactsByResourceAndDate(@Param("dkCode") String dkCode, @Param("date") LocalDate date);

    @Query("SELECT new AlerteServer.dto.RessourceManageDTO(r.dkCode, r.libFonction, r.telPortable, r.email, COUNT(odt)) " +
            "FROM Ressource r " +
            "LEFT JOIN r.ordresDeTravail odt " +
            "GROUP BY r.dkCode, r.libFonction, r.telPortable, r.email")
    List<RessourceManageDTO> findAllManageResources();

    @Query("SELECT new AlerteServer.dto.RessourceManageDTO(r.dkCode, r.libFonction, r.telPortable, r.email, COUNT(odt)) " +
            "FROM Ressource r " +
            "JOIN r.ordresDeTravail odt " +
            "JOIN odt.emplacement s " +
            "WHERE s.departement.num IN :deptNums " +
            "GROUP BY r.dkCode, r.libFonction, r.telPortable, r.email")
    List<RessourceManageDTO> findManageResourcesByDepartments(@Param("deptNums") List<String> deptNums);
}