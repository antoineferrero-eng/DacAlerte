package AlerteServer.dto;

import java.time.LocalDate;
import java.util.Set;

public record BulletinDTO(
        Long id,
        LocalDate date,
        DepartementDTO departement,
        Set<AlerteDTO> alertes,
        Set<DailyMeteoDTO> dailyMeteos
) {}