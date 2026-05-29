package AlerteServer.repository;

import AlerteServer.entity.Bulletin;
import AlerteServer.entity.Daily_meteo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface Daily_meteoRepository extends JpaRepository<Daily_meteo, Long> {
    @Transactional
    void deleteByBulletin(Bulletin bulletin);

    @Modifying
    @Query("DELETE FROM Daily_meteo d WHERE d.bulletin IN (SELECT b FROM Bulletin b WHERE b.date < :dateThreshold)")
    void deleteOldDailyMeteos(@Param("dateThreshold") LocalDate dateThreshold);
}