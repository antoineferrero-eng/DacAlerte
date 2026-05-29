package AlerteServer.service;

import AlerteServer.entity.Bulletin;
import AlerteServer.exception.IdNotFoundException;
import AlerteServer.repository.AlerteRepository;
import AlerteServer.repository.BulletinRepository;
import AlerteServer.repository.Daily_meteoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BulletinService {

    private static final Logger log = LoggerFactory.getLogger(BulletinService.class);

    @Autowired
    private BulletinRepository bulletinRepository;

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private Daily_meteoRepository dailyMeteoRepository;

    public List<Bulletin> getAll() {
        return bulletinRepository.findAllWithDetails();
    }

    public Bulletin getById(Long id) {
        return bulletinRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IdNotFoundException("Bulletin not found: " + id));
    }

    public List<Bulletin> getByDep(String dep) {
        return bulletinRepository.findByDepWithDetails(dep);
    }

    public List<Bulletin> getByDate(String dateStr) {
        return bulletinRepository.findByDateWithDetails(LocalDate.parse(dateStr));
    }

    public List<Bulletin> getByDepAndDate(String dep, String dateStr) {
        return bulletinRepository.findByDepAndDateWithDetails(dep, LocalDate.parse(dateStr));
    }

    public void purgeOldData() {
        LocalDate limitDate = LocalDate.now().minusMonths(1);
        log.info("Purge des bulletins antérieurs au : {}", limitDate);
        try {
            alerteRepository.deleteOldAlertes(limitDate);
            dailyMeteoRepository.deleteOldDailyMeteos(limitDate);
            bulletinRepository.deleteOldBulletins(limitDate);
            log.info("Purge effectuée avec succès.");
        } catch (Exception e) {
            log.error("Erreur lors de la purge des données", e);
        }
    }
}