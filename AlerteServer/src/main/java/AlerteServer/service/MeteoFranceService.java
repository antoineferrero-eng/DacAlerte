package AlerteServer.service;

import AlerteServer.entity.Alerte;
import AlerteServer.entity.Bulletin;
import AlerteServer.entity.Departement;
import AlerteServer.repository.AlerteRepository;
import AlerteServer.repository.BulletinRepository;
import AlerteServer.repository.DepartementRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
public class MeteoFranceService {

    private static final Logger log = LoggerFactory.getLogger(MeteoFranceService.class);

    private final BulletinRepository bulletinRepository;
    private final AlerteRepository alerteRepository;
    private final DepartementRepository departementRepository;
    private final WebClient webClient;

    public MeteoFranceService(BulletinRepository bulletinRepository,
                              AlerteRepository alerteRepository,
                              DepartementRepository departementRepository,
                              WebClient.Builder webClientBuilder) {
        this.bulletinRepository = bulletinRepository;
        this.alerteRepository = alerteRepository;
        this.departementRepository = departementRepository;
        this.webClient = webClientBuilder.build();
    }

    public JsonNode fetchVigilanceData() {
        String username = "bjEoVZLQFh0NXoftNKNKdSK4Zcoa";
        String password = "GGYTw2sjqC5twSV5Kph3TNwAHpka";
        String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");

        JsonNode tokenResponse = webClient.post()
                .uri("https://portail-api.meteofrance.fr/oauth2/token")
                .header("Authorization", "Basic " + auth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError()))
                .block();

        if (tokenResponse == null || !tokenResponse.has("access_token"))
            return null;
        String token = tokenResponse.get("access_token").asText();

        return webClient.get()
                .uri("https://portail-api.meteofrance.fr/public/DPVigilance/v1/cartevigilance/encours")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError()))
                .block();
    }

    @Transactional
    public void processAndSaveVigilanceData(JsonNode json) {
        JsonNode periods = json.path("product").path("periods");
        Set<Long> clearedBulletins = new HashSet<>();

        if (periods.isMissingNode() || !periods.isArray())
            return;

        for (JsonNode period : periods) {
            String echeance = period.path("echeance").asText();
            LocalDate targetDate = echeance.equals("J1") ? LocalDate.now().plusDays(1)
                    : (echeance.equals("J") ? LocalDate.now() : null);

            if (targetDate == null)
                continue;

            JsonNode domainIds = period.path("timelaps").path("domain_ids");
            if (domainIds.isMissingNode() || !domainIds.isArray())
                continue;

            for (JsonNode domainNode : domainIds) {
                String deptNum = domainNode.path("domain_id").asText();
                if (deptNum.equals("FRA"))
                    continue;

                List<String> targetNums = new ArrayList<>();
                if (deptNum.equals("2A") || deptNum.equals("2B")) {
                    targetNums.add("20");
                } else if (deptNum.equals("99")) {
                    targetNums.add("MON");
                    targetNums.add("AND");
                } else {
                    targetNums.add(deptNum);
                }

                for (String finalDeptNum : targetNums) {
                    Departement dept = departementRepository.findById(finalDeptNum).orElseGet(() -> {
                        Departement d = new Departement();
                        d.setNum(finalDeptNum);
                        return departementRepository.save(d);
                    });

                    Bulletin bulletin = bulletinRepository.findByDepartementAndDate(dept, targetDate)
                            .orElseGet(() -> {
                                Bulletin b = new Bulletin();
                                b.setDepartement(dept);
                                b.setDate(targetDate);
                                return bulletinRepository.save(b);
                            });

                    if (!clearedBulletins.contains(bulletin.getId())) {
                        alerteRepository.deleteByBulletin(bulletin);
                        clearedBulletins.add(bulletin.getId());
                    }

                    JsonNode phenomenons = domainNode.path("phenomenon_items");
                    if (phenomenons.isArray()) {
                        for (JsonNode phenomNode : phenomenons) {
                            Alerte alerte = new Alerte();
                            alerte.setType(phenomNode.path("phenomenon_id").asInt());
                            alerte.setLevel(phenomNode.path("phenomenon_max_color_id").asInt());
                            alerte.setBulletin(bulletin);
                            alerteRepository.save(alerte);
                        }
                    }
                }
            }
        }
    }
}
