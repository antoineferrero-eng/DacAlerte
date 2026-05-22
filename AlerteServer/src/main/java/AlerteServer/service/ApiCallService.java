package AlerteServer.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'orchestration pour les tâches asynchrones complexes.
 * Coordonne les appels d'import de données externes (Météo-France, Open-Meteo) et l'envoi d'e-mails d'alerte.
 */
@Service
public class ApiCallService {

    private static final Logger log = LoggerFactory.getLogger(ApiCallService.class);

    private final EmailService emailService;
    private final BulletinService bulletinService;
    private final MeteoFranceService meteoFranceService;
    private final OpenMeteoService openMeteoService;

    public ApiCallService(EmailService emailService,
            BulletinService bulletinService,
            MeteoFranceService meteoFranceService,
            OpenMeteoService openMeteoService) {
        this.emailService = emailService;
        this.bulletinService = bulletinService;
        this.meteoFranceService = meteoFranceService;
        this.openMeteoService = openMeteoService;
    }

    /**
     * Exécute l'importation quotidienne des données météorologiques de manière transactionnelle.
     * Étapes réalisées :
     * 1. Purge des anciens bulletins et données météorologiques obsolètes.
     * 2. Récupération des données de vigilance depuis Météo-France.
     * 3. Traitement et sauvegarde en base des vigilances reçues.
     * 4. Récupération et sauvegarde des données de prévisions détaillées quotidiennes via Open-Meteo.
     */
    @Transactional
    public void runDailyImport() {
        log.info("Lancement de runDailyImport");
        try {
            // Etape 1 : Nettoyage des anciennes données pour libérer de l'espace disque
            bulletinService.purgeOldData();

            // Etape 2 : Récupération des vigilances départementales auprès de Météo-France
            JsonNode vigilanceData = meteoFranceService.fetchVigilanceData();
            if (vigilanceData != null) {
                // Etape 3 : Traitement et persistance des vigilances et bulletins
                meteoFranceService.processAndSaveVigilanceData(vigilanceData);
                
                // Etape 4 : Récupération des prévisions météo détaillées quotidiennes
                openMeteoService.fetchAndSaveAllDailyMeteo();
                
                log.info("Importation complète terminée avec succès");
            }
        } catch (Exception e) {
            log.error("Erreur dans runDailyImport", e);
        }
    }

    /**
     * Déclenche l'envoi des e-mails d'alerte météo à tous les départements concernés.
     * Délègue le traitement interne à l'{@link EmailService}.
     */
    public void sendAlertEmailsToAllDepartments() {
        emailService.sendAlertEmailsToAllDepartments();
    }
}