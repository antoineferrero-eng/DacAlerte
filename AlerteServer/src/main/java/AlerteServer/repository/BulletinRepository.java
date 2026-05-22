package AlerteServer.repository;

import AlerteServer.entity.Bulletin;
import AlerteServer.entity.Departement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BulletinRepository extends JpaRepository<Bulletin, Integer> {

    @Query("SELECT b FROM Bulletin b WHERE b.departement = :departement AND b.date = :date")
    Optional<Bulletin> findByDepartementAndDate(@Param("departement") Departement departement, @Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"departement", "alertes", "dailyMeteos"})
    @Query("SELECT b FROM Bulletin b")
    List<Bulletin> findAllWithDetails();

    @EntityGraph(attributePaths = {"departement", "alertes", "dailyMeteos"})
    @Query("SELECT b FROM Bulletin b WHERE b.id = :id")
    Optional<Bulletin> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = {"departement", "alertes", "dailyMeteos"})
    @Query("SELECT b FROM Bulletin b WHERE b.departement.num = :dep")
    List<Bulletin> findByDepWithDetails(@Param("dep") String dep);

    @EntityGraph(attributePaths = {"departement", "alertes", "dailyMeteos"})
    @Query("SELECT b FROM Bulletin b WHERE b.date = :date")
    List<Bulletin> findByDateWithDetails(@Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"departement", "alertes", "dailyMeteos"})
    @Query("SELECT b FROM Bulletin b WHERE b.departement.num = :dep AND b.date = :date")
    List<Bulletin> findByDepAndDateWithDetails(@Param("dep") String dep, @Param("date") LocalDate date);

    @Modifying
    @Query("DELETE FROM Bulletin b WHERE b.date < :dateThreshold")
    void deleteOldBulletins(@Param("dateThreshold") LocalDate dateThreshold);
}