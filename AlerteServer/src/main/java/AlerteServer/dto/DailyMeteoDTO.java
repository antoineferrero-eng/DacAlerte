package AlerteServer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyMeteoDTO(
        Long id,
        LocalDate date,
        Integer weatherCode,
        Double tempMax,
        Double tempMin,
        Double apparentTempMax,
        Double apparentTempMin,
        LocalDateTime sunrise,
        LocalDateTime sunset,
        Double daylightDuration,
        Double sunshineDuration,
        Double uvIndexMax,
        Double uvIndexClearSkyMax,
        Double rainSum,
        Double showersSum,
        Double snowfallSum,
        Double precipitationSum,
        Double precipitationHours,
        Integer precipitationProbabilityMax,
        Double windSpeedMax,
        Double windGustsMax,
        Integer windDirectionDominant,
        Double shortwaveRadiationSum,
        Double evapotranspiration
) {}