package AlerteServer.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

    private final WebClient webClient;

    public MeteoFranceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public record Phenomenon(int typeId, int levelId) {}
    public record VigilanceResult(String departementNum, LocalDate targetDate, List<Phenomenon> phenomenons) {}

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

    public List<VigilanceResult> parseVigilanceData(JsonNode json) {
        List<VigilanceResult> results = new ArrayList<>();
        JsonNode periods = json.path("product").path("periods");

        if (periods.isMissingNode() || !periods.isArray())
            return results;

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

                if (deptNum.length() == 4 && deptNum.endsWith("10")) {
                    deptNum = deptNum.substring(0, 2);
                }

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
                    List<Phenomenon> phenoms = new ArrayList<>();
                    JsonNode phenomenons = domainNode.path("phenomenon_items");
                    if (phenomenons.isArray()) {
                        for (JsonNode phenomNode : phenomenons) {
                            phenoms.add(new Phenomenon(
                                phenomNode.path("phenomenon_id").asInt(),
                                phenomNode.path("phenomenon_max_color_id").asInt()
                            ));
                        }
                    }
                    results.add(new VigilanceResult(finalDeptNum, targetDate, phenoms));
                }
            }
        }
        return results;
    }
}
