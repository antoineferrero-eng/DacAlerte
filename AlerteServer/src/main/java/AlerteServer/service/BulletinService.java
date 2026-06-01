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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import AlerteServer.entity.Alerte;
import AlerteServer.entity.Departement;
import AlerteServer.repository.DepartementRepository;
import AlerteServer.service.MeteoFranceService.VigilanceResult;
import AlerteServer.service.MeteoFranceService.Phenomenon;

@Service
public class BulletinService {

    private static final Logger log = LoggerFactory.getLogger(BulletinService.class);

    @Autowired
    private BulletinRepository bulletinRepository;

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private Daily_meteoRepository dailyMeteoRepository;

    @Autowired
    private DepartementRepository departementRepository;

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

    @Transactional
    public void saveVigilances(List<VigilanceResult> vigilances) {
        Set<Long> clearedBulletins = new HashSet<>();
        for (VigilanceResult vr : vigilances) {
            Departement dept = departementRepository.findById(vr.departementNum()).orElseGet(() -> {
                Departement d = new Departement();
                d.setNum(vr.departementNum());
                return departementRepository.save(d);
            });

            Bulletin bulletin = bulletinRepository.findByDepartementAndDate(dept, vr.targetDate())
                    .orElseGet(() -> {
                        Bulletin b = new Bulletin();
                        b.setDepartement(dept);
                        b.setDate(vr.targetDate());
                        return bulletinRepository.save(b);
                    });

            if (!clearedBulletins.contains(bulletin.getId())) {
                alerteRepository.deleteByBulletin(bulletin);
                clearedBulletins.add(bulletin.getId());
            }

            for (Phenomenon p : vr.phenomenons()) {
                Alerte alerte = new Alerte();
                alerte.setType(p.typeId());
                alerte.setLevel(p.levelId());
                alerte.setBulletin(bulletin);
                alerteRepository.save(alerte);
            }
        }
    }
}