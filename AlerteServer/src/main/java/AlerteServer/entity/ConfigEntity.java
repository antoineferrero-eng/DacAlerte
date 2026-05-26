package AlerteServer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "app_config")
public class ConfigEntity {

    @Id
    private Long id = 1L;

    @Column(name = "active_levels", length = 500)
    private String activeLevelsStr;

    @Column(name = "active_types", length = 500)
    private String activeTypesStr;

    @Column(name = "mail_cron")
    private String mailCron;

    @Column(name = "update_cron")
    private String updateCron;

    public ConfigEntity() {
    }

    public ConfigEntity(List<String> activeLevels, List<String> activeTypes, String mailCron, String updateCron) {
        setActiveLevels(activeLevels);
        setActiveTypes(activeTypes);
        this.mailCron = mailCron;
        this.updateCron = updateCron;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getActiveLevels() {
        if (activeLevelsStr == null || activeLevelsStr.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(activeLevelsStr.split(",")));
    }

    public void setActiveLevels(List<String> activeLevels) {
        if (activeLevels == null || activeLevels.isEmpty()) {
            this.activeLevelsStr = "";
        } else {
            this.activeLevelsStr = String.join(",", activeLevels);
        }
    }

    public List<String> getActiveTypes() {
        if (activeTypesStr == null || activeTypesStr.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(activeTypesStr.split(",")));
    }

    public void setActiveTypes(List<String> activeTypes) {
        if (activeTypes == null || activeTypes.isEmpty()) {
            this.activeTypesStr = "";
        } else {
            this.activeTypesStr = String.join(",", activeTypes);
        }
    }

    public String getMailCron() {
        return mailCron;
    }

    public void setMailCron(String mailCron) {
        this.mailCron = mailCron;
    }

    public String getUpdateCron() {
        return updateCron;
    }

    public void setUpdateCron(String updateCron) {
        this.updateCron = updateCron;
    }
}
