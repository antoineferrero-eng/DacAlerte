package AlerteServer.service;

import AlerteServer.config.AppConfig;
import AlerteServer.entity.ConfigEntity;
import AlerteServer.repository.ConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {

    private final ConfigRepository configRepository;
    private final AppConfig appConfigProperties;

    public ConfigService(ConfigRepository configRepository, AppConfig appConfigProperties) {
        this.configRepository = configRepository;
        this.appConfigProperties = appConfigProperties;
    }

    @PostConstruct
    public void init() {
        if (configRepository.count() == 0) {
            ConfigEntity entity = new ConfigEntity(
                    appConfigProperties.getActiveLevels(),
                    appConfigProperties.getActiveTypes(),
                    appConfigProperties.getMailCron(),
                    appConfigProperties.getUpdateCron()
            );
            configRepository.save(entity);
        }
    }

    public ConfigEntity getConfigEntity() {
        return configRepository.findById(1L).orElseThrow(() -> new RuntimeException("Configuration introuvable en base de données"));
    }

    public List<String> getActiveLevels() {
        return getConfigEntity().getActiveLevels();
    }

    public void setActiveLevels(List<String> levels) {
        ConfigEntity entity = getConfigEntity();
        entity.setActiveLevels(levels);
        configRepository.save(entity);
    }

    public List<String> getActiveTypes() {
        return getConfigEntity().getActiveTypes();
    }

    public void setActiveTypes(List<String> types) {
        ConfigEntity entity = getConfigEntity();
        entity.setActiveTypes(types);
        configRepository.save(entity);
    }

    public String getMailCron() {
        return getConfigEntity().getMailCron();
    }

    public void setMailCron(String cron) {
        ConfigEntity entity = getConfigEntity();
        entity.setMailCron(cron);
        configRepository.save(entity);
    }

    public String getUpdateCron() {
        return getConfigEntity().getUpdateCron();
    }

    public void setUpdateCron(String cron) {
        ConfigEntity entity = getConfigEntity();
        entity.setUpdateCron(cron);
        configRepository.save(entity);
    }
}
