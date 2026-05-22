package AlerteServer.controller;

import AlerteServer.dto.AlerteDTO;
import AlerteServer.dto.BulletinDTO;
import AlerteServer.dto.DailyMeteoDTO;
import AlerteServer.dto.DepartementDTO;
import AlerteServer.entity.Bulletin;
import AlerteServer.entity.Daily_meteo;
import AlerteServer.service.BulletinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bulletins")
public class BulletinController {

    @Autowired
    private BulletinService bulletinService;

    @GetMapping
    public List<BulletinDTO> getBulletins(
            @RequestParam(required = false) String dep,
            @RequestParam(required = false) String date) {

        List<Bulletin> bulletins;
        if (dep != null && date != null) {
            bulletins = bulletinService.getByDepAndDate(dep, date);
        } else if (dep != null) {
            bulletins = bulletinService.getByDep(dep);
        } else if (date != null) {
            bulletins = bulletinService.getByDate(date);
        } else {
            bulletins = bulletinService.getAll();
        }

        return bulletins.stream()
                .map(this::mapToDetailDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public BulletinDTO getById(@PathVariable Long id) {
        return mapToDetailDTO(bulletinService.getById(id));
    }

    private BulletinDTO mapToDetailDTO(Bulletin bulletin) {
        DepartementDTO depDTO = null;
        if (bulletin.getDepartement() != null) {
            depDTO = new DepartementDTO(
                    bulletin.getDepartement().getNum(),
                    bulletin.getDepartement().getLat(),
                    bulletin.getDepartement().getLongitude()
            );
        }

        Set<AlerteDTO> alertesDTO = bulletin.getAlertes().stream()
                .map(a -> new AlerteDTO(a.getId(), a.getType(), a.getLevel(), bulletin.getId()))
                .collect(Collectors.toSet());

        Set<DailyMeteoDTO> meteosDTO = bulletin.getDailyMeteos().stream()
                .map(this::mapToMeteoDTO)
                .collect(Collectors.toSet());

        return new BulletinDTO(
                bulletin.getId(),
                bulletin.getDate(),
                depDTO,
                alertesDTO,
                meteosDTO
        );
    }

    private DailyMeteoDTO mapToMeteoDTO(Daily_meteo m) {
        return new DailyMeteoDTO(
                m.getId(),
                m.getDate(),
                m.getWeatherCode(),
                m.getTempMax(),
                m.getTempMin(),
                m.getApparentTempMax(),
                m.getApparentTempMin(),
                m.getSunrise(),
                m.getSunset(),
                m.getDaylightDuration(),
                m.getSunshineDuration(),
                m.getUvIndexMax(),
                m.getUvIndexClearSkyMax(),
                m.getRainSum(),
                m.getShowersSum(),
                m.getSnowfallSum(),
                m.getPrecipitationSum(),
                m.getPrecipitationHours(),
                m.getPrecipitationProbabilityMax(),
                m.getWindSpeedMax(),
                m.getWindGustsMax(),
                m.getWindDirectionDominant(),
                m.getShortwaveRadiationSum(),
                m.getEvapotranspiration()
        );
    }
}