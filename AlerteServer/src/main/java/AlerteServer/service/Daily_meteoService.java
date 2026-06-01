package AlerteServer.service;

import AlerteServer.entity.Daily_meteo;
import AlerteServer.exception.IdNotFoundException;
import AlerteServer.repository.BulletinRepository;
import AlerteServer.repository.Daily_meteoRepository;
import AlerteServer.repository.DepartementRepository;
import AlerteServer.service.OpenMeteoService.ForecastResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class Daily_meteoService {

    @Autowired
    private Daily_meteoRepository dailyMeteoRepository;

    @Autowired
    private BulletinRepository bulletinRepository;

    @Autowired
    private DepartementRepository departementRepository;

    public List<Daily_meteo> getAll() {
        return dailyMeteoRepository.findAll();
    }

    public Daily_meteo getById(Long id) {
        return dailyMeteoRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Daily-meteo not found: " + id));
    }

    @Transactional
    public void saveForecasts(List<ForecastResult> forecasts) {
        for (ForecastResult fr : forecasts) {
            departementRepository.findById(fr.departementNum()).ifPresent(targetDept -> {
                bulletinRepository.findByDepartementAndDate(targetDept, fr.date()).ifPresent(bulletin -> {
                    dailyMeteoRepository.deleteByBulletin(bulletin);
                    Daily_meteo dm = fr.meteo();
                    dm.setBulletin(bulletin);
                    dailyMeteoRepository.save(dm);
                });
            });
        }
    }
}
