package AlerteServer.service;

import AlerteServer.entity.Daily_meteo;
import AlerteServer.entity.Departement;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenMeteoService {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoService.class);

    private final WebClient webClient;

    public OpenMeteoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public record ForecastResult(String departementNum, LocalDate date, Daily_meteo meteo) {}

    public List<ForecastResult> fetchForecasts(List<Departement> depts) {
        List<ForecastResult> allResults = new ArrayList<>();
        List<Departement> validDepts = depts.stream()
                .filter(d -> d.getLat() != null && d.getLongitude() != null)
                .toList();

        for (int i = 0; i < validDepts.size(); i += 50) {
            List<Departement> batch = validDepts.subList(i, Math.min(i + 50, validDepts.size()));
            String lats = batch.stream().map(d -> d.getLat().toString().replace(",", "."))
                    .collect(Collectors.joining(","));
            String longs = batch.stream().map(d -> d.getLongitude().toString().replace(",", "."))
                    .collect(Collectors.joining(","));

            String uri = "https://api.open-meteo.com/v1/forecast?latitude=" + lats + "&longitude=" + longs +
                    "&daily=temperature_2m_max,weather_code,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,daylight_duration,sunshine_duration,uv_index_max,uv_index_clear_sky_max,rain_sum,showers_sum,snowfall_sum,precipitation_sum,precipitation_hours,precipitation_probability_max,wind_speed_10m_max,wind_gusts_10m_max,wind_direction_10m_dominant,shortwave_radiation_sum,et0_fao_evapotranspiration&past_days=0&forecast_days=2";

            JsonNode response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof WebClientResponseException &&
                                    ((WebClientResponseException) throwable).getStatusCode().is5xxServerError()))
                    .block();

            if (response != null) {
                if (response.isArray()) {
                    for (int j = 0; j < response.size(); j++) {
                        allResults.addAll(parseDailyMeteoForIndex(batch.get(j), response.get(j), 0, LocalDate.now()));
                        allResults.addAll(parseDailyMeteoForIndex(batch.get(j), response.get(j), 1, LocalDate.now().plusDays(1)));
                    }
                } else {
                    allResults.addAll(parseDailyMeteoForIndex(batch.get(0), response, 0, LocalDate.now()));
                    allResults.addAll(parseDailyMeteoForIndex(batch.get(0), response, 1, LocalDate.now().plusDays(1)));
                }
            }
        }
        return allResults;
    }

    private List<ForecastResult> parseDailyMeteoForIndex(Departement dept, JsonNode fullNode, int index, LocalDate date) {
        List<ForecastResult> results = new ArrayList<>();
        List<String> targetCodes = "99".equals(dept.getNum()) ? List.of("99A", "99B") : List.of(dept.getNum());
        
        JsonNode dailyData = fullNode.path("daily");
        if (!dailyData.isMissingNode() && dailyData.path("time").isArray() && dailyData.path("time").size() > index) {
            for (String code : targetCodes) {
                Daily_meteo dm = new Daily_meteo();
                dm.setDate(date);
                dm.setWeatherCode(getSafeInt(dailyData, "weather_code", index));
                dm.setTempMax(getSafeDouble(dailyData, "temperature_2m_max", index));
                dm.setTempMin(getSafeDouble(dailyData, "temperature_2m_min", index));
                dm.setApparentTempMax(getSafeDouble(dailyData, "apparent_temperature_max", index));
                dm.setApparentTempMin(getSafeDouble(dailyData, "apparent_temperature_min", index));

                String sunriseStr = getSafeString(dailyData, "sunrise", index);
                if (sunriseStr != null) dm.setSunrise(LocalDateTime.parse(sunriseStr));

                String sunsetStr = getSafeString(dailyData, "sunset", index);
                if (sunsetStr != null) dm.setSunset(LocalDateTime.parse(sunsetStr));

                dm.setDaylightDuration(getSafeDouble(dailyData, "daylight_duration", index));
                dm.setSunshineDuration(getSafeDouble(dailyData, "sunshine_duration", index));
                dm.setUvIndexMax(getSafeDouble(dailyData, "uv_index_max", index));
                dm.setUvIndexClearSkyMax(getSafeDouble(dailyData, "uv_index_clear_sky_max", index));
                dm.setRainSum(getSafeDouble(dailyData, "rain_sum", index));
                dm.setShowersSum(getSafeDouble(dailyData, "showers_sum", index));
                dm.setSnowfallSum(getSafeDouble(dailyData, "snowfall_sum", index));
                dm.setPrecipitationSum(getSafeDouble(dailyData, "precipitation_sum", index));
                dm.setPrecipitationHours(getSafeDouble(dailyData, "precipitation_hours", index));
                dm.setPrecipitationProbabilityMax(getSafeInt(dailyData, "precipitation_probability_max", index));
                dm.setWindSpeedMax(getSafeDouble(dailyData, "wind_speed_10m_max", index));
                dm.setWindGustsMax(getSafeDouble(dailyData, "wind_gusts_10m_max", index));
                dm.setWindDirectionDominant(getSafeInt(dailyData, "wind_direction_10m_dominant", index));
                dm.setShortwaveRadiationSum(getSafeDouble(dailyData, "shortwave_radiation_sum", index));
                dm.setEvapotranspiration(getSafeDouble(dailyData, "et0_fao_evapotranspiration", index));

                results.add(new ForecastResult(code, date, dm));
            }
        }
        return results;
    }

    private Double getSafeDouble(JsonNode parent, String fieldName, int index) {
        JsonNode arrayNode = parent.path(fieldName);
        if (arrayNode.isArray() && arrayNode.size() > index && !arrayNode.get(index).isNull()) {
            return arrayNode.get(index).asDouble();
        }
        return null;
    }

    private Integer getSafeInt(JsonNode parent, String fieldName, int index) {
        JsonNode arrayNode = parent.path(fieldName);
        if (arrayNode.isArray() && arrayNode.size() > index && !arrayNode.get(index).isNull()) {
            return arrayNode.get(index).asInt();
        }
        return null;
    }

    private String getSafeString(JsonNode parent, String fieldName, int index) {
        JsonNode arrayNode = parent.path(fieldName);
        if (arrayNode.isArray() && arrayNode.size() > index && !arrayNode.get(index).isNull()) {
            return arrayNode.get(index).asText();
        }
        return null;
    }
}
