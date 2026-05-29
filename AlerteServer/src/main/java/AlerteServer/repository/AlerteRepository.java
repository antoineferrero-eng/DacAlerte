package AlerteServer.repository;

import AlerteServer.entity.Alerte;
import AlerteServer.entity.Bulletin;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface AlerteRepository extends JpaRepository<Alerte, Integer> {
    @Transactional
    void deleteByBulletin(Bulletin bulletin);

    @Modifying
    @Query("DELETE FROM Alerte a WHERE a.bulletin IN (SELECT b FROM Bulletin b WHERE b.date < :dateThreshold)")
    void deleteOldAlertes(@Param("dateThreshold") LocalDate dateThreshold);
}
