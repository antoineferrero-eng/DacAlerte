package AlerteServer.repository;

import AlerteServer.entity.Alerte;
import AlerteServer.entity.Bulletin;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlerteRepository extends JpaRepository<Alerte, Integer> {
    @Transactional
    void deleteByBulletin(Bulletin bulletin);
}
