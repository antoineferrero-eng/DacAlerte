package AlerteServer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppConfig {

    private List<String> activeLevels;
    private List<String> activeTypes;
    private String mailCron;
    private String updateCron;

    public AppConfig(
            @Value("${alerte.niveaux.actifs}") List<String> activeLevels,
            @Value("${alerte.types.actifs}") List<String> activeTypes,
            @Value("${alerte.mail.cron}") String mailCron,
            @Value("${alerte.update.cron}") String updateCron
    ) {
        this.activeLevels = activeLevels.stream().map(String::trim).collect(Collectors.toCollection(ArrayList::new));
        this.activeTypes = activeTypes.stream().map(String::trim).collect(Collectors.toCollection(ArrayList::new));
        this.mailCron = mailCron.trim();
        this.updateCron = updateCron.trim();
    }

    public List<String> getActiveLevels() {
        return activeLevels;
    }

    public void setActiveLevels(List<String> activeLevels) {
        this.activeLevels = activeLevels;
    }

    public List<String> getActiveTypes() {
        return activeTypes;
    }

    public void setActiveTypes(List<String> activeTypes) {
        this.activeTypes = activeTypes;
    }

    public String getMailCron() {
        return mailCron;
    }

    public void setMailCron(String mailCron) {
        if (mailCron != null && !mailCron.contains("NaN")) {
            this.mailCron = mailCron.trim();
        }
    }

    public String getUpdateCron() {
        return updateCron;
    }

    public void setUpdateCron(String updateCron) {
        if (updateCron != null && !updateCron.contains("NaN")) {
            this.updateCron = updateCron.trim();
        }
    }
}
