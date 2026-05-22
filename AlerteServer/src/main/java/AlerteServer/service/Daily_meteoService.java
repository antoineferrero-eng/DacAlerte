package AlerteServer.service;

import AlerteServer.entity.Daily_meteo;
import AlerteServer.exception.IdNotFoundException;
import AlerteServer.repository.Daily_meteoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Daily_meteoService {

    @Autowired
    private Daily_meteoRepository dailyMeteoRepository;

    public List<Daily_meteo> getAll() {
        return dailyMeteoRepository.findAll();
    }

    public Daily_meteo getById(Long id) {
        return dailyMeteoRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Daily-meteo not found: " + id));
    }
}
