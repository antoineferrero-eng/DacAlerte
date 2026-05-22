package AlerteServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration de l'infrastructure web de l'application.
 * Implémente {@link WebMvcConfigurer} pour configurer les intercepteurs CORS et
 * MVC,
 * et définit les beans pour le client Web réactif (WebClient) et la
 * planification des tâches.
 */
@Configuration
public class WebClientConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;

    /**
     * Enregistre les intercepteurs Spring MVC.
     * Configure {@link AdminInterceptor} pour protéger l'ensemble des requêtes
     * ciblées sur `/config/**`.
     *
     * @param registry le registre permettant d'ajouter et configurer les
     *                 intercepteurs
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/config/**");
    }

    /**
     * Déclare un Bean WebClient réactif pré-configuré avec l'URL de base de l'API
     * Open-Meteo.
     * Utilisé pour effectuer des appels HTTP non bloquants vers les services
     * météorologiques externes.
     *
     * @param builder le builder Spring injecté pour construire l'instance WebClient
     * @return un client Web pré-configuré
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.open-meteo.com/")
                .build();
    }

    /**
     * Configure et fournit un planificateur de tâches threadé (TaskScheduler).
     * Gère l'exécution asynchrone et planifiée (cron) des tâches de fond de
     * l'application (import météo, emails).
     *
     * @return un TaskScheduler initialisé avec un pool de 5 threads
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("alerte-scheduler-");
        scheduler.initialize();
        return scheduler;
    }

    /**
     * Définit les règles CORS (Cross-Origin Resource Sharing) pour l'application.
     * Autorise explicitement l'application frontend Angular en local à consommer
     * cette API.
     *
     * @param registry le registre des correspondances CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:4200", "http://141.253.96.114", "http://141.253.96.114:*", "https://dacalerte.duckdns.org")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}