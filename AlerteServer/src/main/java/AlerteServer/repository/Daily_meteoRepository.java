package AlerteServer.repository;

import AlerteServer.entity.Bulletin;
import AlerteServer.entity.Daily_meteo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface Daily_meteoRepository extends JpaRepository<Daily_meteo, Long> {
    @Transactional
    void deleteByBulletin(Bulletin bulletin);
}