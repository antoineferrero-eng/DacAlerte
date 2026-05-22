package AlerteServer.controller;

import AlerteServer.entity.Daily_meteo;
import AlerteServer.service.Daily_meteoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/daily-meteos")
public class Daily_meteoController {

    @Autowired
    private Daily_meteoService dailyMeteoService;

    @GetMapping
    public List<Daily_meteo> getAll() {
        return dailyMeteoService.getAll();
    }

    @GetMapping("/{id}")
    public Daily_meteo getById(@PathVariable Long id) {
        return dailyMeteoService.getById(id);
    }

}
