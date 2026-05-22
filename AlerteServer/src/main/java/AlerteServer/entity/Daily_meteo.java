package AlerteServer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_meteo")
public class Daily_meteo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_bulletin")
    @JsonIgnore
    private Bulletin bulletin;

    @Column(name = "date_meteo")
    private LocalDate date;

    @Column(name = "weather_code")
    private Integer weatherCode;

    @Column(name = "temp_max")
    private Double tempMax;

    @Column(name = "temp_min")
    private Double tempMin;

    @Column(name = "apparent_temp_max")
    private Double apparentTempMax;

    @Column(name = "apparent_temp_min")
    private Double apparentTempMin;

    private LocalDateTime sunrise;

    private LocalDateTime sunset;

    @Column(name = "daylight_duration")
    private Double daylightDuration;

    @Column(name = "sunshine_duration")
    private Double sunshineDuration;

    @Column(name = "uv_index_max")
    private Double uvIndexMax;

    @Column(name = "uv_index_clear_sky_max")
    private Double uvIndexClearSkyMax;

    @Column(name = "rain_sum")
    private Double rainSum;

    @Column(name = "showers_sum")
    private Double showersSum;

    @Column(name = "snowfall_sum")
    private Double snowfallSum;

    @Column(name = "precipitation_sum")
    private Double precipitationSum;

    @Column(name = "precipitation_hours")
    private Double precipitationHours;

    @Column(name = "precipitation_probability_max")
    private Integer precipitationProbabilityMax;

    @Column(name = "wind_speed_max")
    private Double windSpeedMax;

    @Column(name = "wind_gusts_max")
    private Double windGustsMax;

    @Column(name = "wind_direction_dominant")
    private Integer windDirectionDominant;

    @Column(name = "shortwave_radiation_sum")
    private Double shortwaveRadiationSum;

    @Column(name = "evapotranspiration")
    private Double evapotranspiration;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Bulletin getBulletin() { return bulletin; }
    public void setBulletin(Bulletin bulletin) { this.bulletin = bulletin; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Integer getWeatherCode() { return weatherCode; }
    public void setWeatherCode(Integer weatherCode) { this.weatherCode = weatherCode; }
    public Double getTempMax() { return tempMax; }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }
    public Double getTempMin() { return tempMin; }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }
    public Double getApparentTempMax() { return apparentTempMax; }
    public void setApparentTempMax(Double apparentTempMax) { this.apparentTempMax = apparentTempMax; }
    public Double getApparentTempMin() { return apparentTempMin; }
    public void setApparentTempMin(Double apparentTempMin) { this.apparentTempMin = apparentTempMin; }
    public LocalDateTime getSunrise() { return sunrise; }
    public void setSunrise(LocalDateTime sunrise) { this.sunrise = sunrise; }
    public LocalDateTime getSunset() { return sunset; }
    public void setSunset(LocalDateTime sunset) { this.sunset = sunset; }
    public Double getDaylightDuration() { return daylightDuration; }
    public void setDaylightDuration(Double daylightDuration) { this.daylightDuration = daylightDuration; }
    public Double getSunshineDuration() { return sunshineDuration; }
    public void setSunshineDuration(Double sunshineDuration) { this.sunshineDuration = sunshineDuration; }
    public Double getUvIndexMax() { return uvIndexMax; }
    public void setUvIndexMax(Double uvIndexMax) { this.uvIndexMax = uvIndexMax; }
    public Double getUvIndexClearSkyMax() { return uvIndexClearSkyMax; }
    public void setUvIndexClearSkyMax(Double uvIndexClearSkyMax) { this.uvIndexClearSkyMax = uvIndexClearSkyMax; }
    public Double getRainSum() { return rainSum; }
    public void setRainSum(Double rainSum) { this.rainSum = rainSum; }
    public Double getShowersSum() { return showersSum; }
    public void setShowersSum(Double showersSum) { this.showersSum = showersSum; }
    public Double getSnowfallSum() { return snowfallSum; }
    public void setSnowfallSum(Double snowfallSum) { this.snowfallSum = snowfallSum; }
    public Double getPrecipitationSum() { return precipitationSum; }
    public void setPrecipitationSum(Double precipitationSum) { this.precipitationSum = precipitationSum; }
    public Double getPrecipitationHours() { return precipitationHours; }
    public void setPrecipitationHours(Double precipitationHours) { this.precipitationHours = precipitationHours; }
    public Integer getPrecipitationProbabilityMax() { return precipitationProbabilityMax; }
    public void setPrecipitationProbabilityMax(Integer precipitationProbabilityMax) { this.precipitationProbabilityMax = precipitationProbabilityMax; }
    public Double getWindSpeedMax() { return windSpeedMax; }
    public void setWindSpeedMax(Double windSpeedMax) { this.windSpeedMax = windSpeedMax; }
    public Double getWindGustsMax() { return windGustsMax; }
    public void setWindGustsMax(Double windGustsMax) { this.windGustsMax = windGustsMax; }
    public Integer getWindDirectionDominant() { return windDirectionDominant; }
    public void setWindDirectionDominant(Integer windDirectionDominant) { this.windDirectionDominant = windDirectionDominant; }
    public Double getShortwaveRadiationSum() { return shortwaveRadiationSum; }
    public void setShortwaveRadiationSum(Double shortwaveRadiationSum) { this.shortwaveRadiationSum = shortwaveRadiationSum; }
    public Double getEvapotranspiration() { return evapotranspiration; }
    public void setEvapotranspiration(Double evapotranspiration) { this.evapotranspiration = evapotranspiration; }
}