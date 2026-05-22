package AlerteServer.service;

import AlerteServer.config.AppConfig;
import AlerteServer.dto.ContactAlerteDTO;
import AlerteServer.entity.Departement;
import AlerteServer.repository.DepartementRepository;
import AlerteServer.repository.RessourceRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de gestion et d'envoi des notifications par e-mail.
 * Responsable de la construction des e-mails HTML d'alerte météo, du filtrage
 * des alertes actives,
 * et de l'envoi de ces e-mails via SMTP aux ressources (intervenants) ayant des
 * interventions planifiées.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final RessourceRepository ressourceRepository;
    private final DepartementRepository departementRepository;
    private final AppConfig appConfig;

    public EmailService(JavaMailSender mailSender,
            RessourceRepository ressourceRepository,
            DepartementRepository departementRepository,
            AppConfig appConfig) {
        this.mailSender = mailSender;
        this.ressourceRepository = ressourceRepository;
        this.departementRepository = departementRepository;
        this.appConfig = appConfig;
    }

    /**
     * Envoie un e-mail au format HTML à un destinataire donné.
     * Utilise JavaMailSender et configure l'expéditeur, le destinataire, l'objet et
     * le contenu.
     *
     * @param to          l'adresse e-mail du destinataire
     * @param subject     l'objet du message
     * @param htmlContent le corps du message formaté en HTML
     * @throws RuntimeException en cas d'erreur de messagerie (ex: SMTP
     *                          inaccessible)
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("dacvigilance@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoie les e-mails d'alerte météo pour un département spécifique à une date
     * donnée.
     * Détermine les contacts concernés (intervenants planifiés dans ce
     * département),
     * regroupe les alertes par adresse email, vérifie si elles correspondent aux
     * filtres actifs,
     * et envoie un e-mail HTML récapitulatif à chaque personne concernée.
     *
     * @param dateStr la date de l'intervention au format "AAAA-MM-JJ"
     * @param deptNum le numéro du département (ex: "75", "06")
     */
    public void sendAlertEmails(String dateStr, String deptNum) {
        LocalDate date = LocalDate.parse(dateStr);
        List<ContactAlerteDTO> contacts = ressourceRepository.findContactsByAlerte(date, deptNum);

        Map<String, List<ContactAlerteDTO>> alertsByEmail = new HashMap<>();

        for (ContactAlerteDTO contact : contacts) {
            String email = contact.email();
            if (email != null && !email.isEmpty() && !"null".equals(email)) {
                alertsByEmail.computeIfAbsent(email, k -> new ArrayList<>()).add(contact);
            }
        }

        List<String> activeLevels = appConfig.getActiveLevels();
        List<String> activeTypes = appConfig.getActiveTypes();

        for (Map.Entry<String, List<ContactAlerteDTO>> entry : alertsByEmail.entrySet()) {
            boolean hasAlert = entry.getValue().stream()
                    .anyMatch(a -> activeLevels.contains(String.valueOf(a.niveau()))
                            && activeTypes.contains(String.valueOf(a.type())));

            if (hasAlert) {
                String email = entry.getKey();
                String message = buildMessage(entry.getValue(), deptNum);
                sendHtmlEmail(email, "ALERTE MÉTÉO", message);
            }
        }
    }

    /**
     * Envoie un e-mail HTML d'alerte manuelle pour une ressource spécifique,
     * en listant ses vigilances actives.
     *
     * @param dkCode l'identifiant unique de la ressource
     * @param to     l'adresse email de la ressource
     * @param alerts la liste de toutes ses alertes météo actives
     */
    public void sendManualAlertEmail(String dkCode, String to, List<ContactAlerteDTO> alerts) {
        List<String> activeLevels = appConfig.getActiveLevels();
        List<String> activeTypes = appConfig.getActiveTypes();

        StringBuilder sb = new StringBuilder();
        sb.append(
                "<html><body style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; padding: 20px; margin: 0;\">");
        sb.append(
                "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">");
        sb.append("<div style=\"background-color: #2c3e50; color: #ffffff; padding: 20px; text-align: center;\">");
        sb.append("<h2 style=\"margin: 0; font-size: 24px;\">Vigilance Météo</h2>");
        sb.append("<p style=\"margin: 5px 0 0 0; font-size: 16px; opacity: 0.9;\">Intervenant : ").append(dkCode)
                .append("</p>");
        sb.append("</div>");
        sb.append("<div style=\"padding: 30px;\">");
        sb.append("<p style=\"font-size: 16px; color: #333333; margin-top: 0;\">Bonjour,</p>");
        sb.append(
                "<p style=\"font-size: 16px; color: #555555; line-height: 1.5;\">Ceci est un rappel de vigilance envoyé manuellement concernant vos interventions du jour.</p>");

        boolean hasAlerts = false;
        if (alerts != null && !alerts.isEmpty()) {
            sb.append(
                    "<h3 style=\"color: #2c3e50; margin-top: 25px; border-bottom: 2px solid #ecf0f1; padding-bottom: 10px;\">Récapitulatif des alertes actives :</h3>");
            for (ContactAlerteDTO alert : alerts) {
                String levelValue = String.valueOf(alert.niveau());
                String typeValue = String.valueOf(alert.type());

                if (activeLevels.contains(levelValue) && activeTypes.contains(typeValue)) {
                    hasAlerts = true;
                    String type = getAlertTypeName(typeValue);
                    String advice = getAlertAdvice(typeValue);
                    String level = getAlertLevelName(levelValue);
                    String color = getColor(levelValue);
                    String bgColor = getBgColor(levelValue);

                    sb.append("<div style=\"margin-bottom: 20px; padding: 15px; border-left: 5px solid ").append(color)
                            .append("; background-color: ").append(bgColor).append("; border-radius: 0 6px 6px 0;\">");
                    sb.append("<h4 style=\"margin: 0 0 8px 0; color: ").append(color).append("; font-size: 18px;\">")
                            .append(type).append(" - Niveau ").append(level).append("</h4>");
                    sb.append(
                            "<p style=\"margin: 0; font-size: 15px; color: #444444; line-height: 1.5;\"><strong>Conseil : </strong>")
                            .append(advice).append("</p>");
                    sb.append("</div>");
                }
            }
        }

        if (!hasAlerts) {
            sb.append(
                    "<div style=\"padding: 15px; background-color: #eafaf1; border-left: 5px solid #27ae60; color: #27ae60; border-radius: 6px; margin: 20px 0;\">");
            sb.append(
                    "<p style=\"margin: 0; font-weight: bold;\">Aucune vigilance météorologique active n'est signalée sur vos chantiers planifiés pour aujourd'hui.</p>");
            sb.append("</div>");
        }

        sb.append(
                "<p style=\"font-size: 16px; color: #555555; margin-top: 30px; padding-top: 20px; border-top: 1px solid #ecf0f1;\">Merci de prendre les précautions nécessaires avant votre déplacement.</p>");
        sb.append("<p style=\"font-size: 16px; color: #333333; font-weight: bold;\">Merci de votre compréhension.</p>");
        sb.append("</div></div></body></html>");

        sendHtmlEmail(to, "ALERTE MÉTÉO", sb.toString());
    }

    /**
     * Construit le corps de l'e-mail HTML destiné à un intervenant.
     * Génère un récapitulatif visuel et stylisé avec les conseils professionnels
     * associés
     * pour chaque type de vigilance météo active.
     *
     * @param alerts  la liste des alertes concernant cet intervenant
     * @param deptNum le numéro du département concerné
     * @return une chaîne contenant le code HTML de l'e-mail
     */
    private String buildMessage(List<ContactAlerteDTO> alerts, String deptNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "<html><body style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; padding: 20px; margin: 0;\">");
        sb.append(
                "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">");
        sb.append("<div style=\"background-color: #2c3e50; color: #ffffff; padding: 20px; text-align: center;\">");
        sb.append("<h2 style=\"margin: 0; font-size: 24px;\">Vigilance Météo</h2>");
        sb.append("<p style=\"margin: 5px 0 0 0; font-size: 16px; opacity: 0.9;\">Département ").append(deptNum)
                .append("</p>");
        sb.append("</div>");
        sb.append("<div style=\"padding: 30px;\">");
        sb.append("<p style=\"font-size: 16px; color: #333333; margin-top: 0;\">Bonjour,</p>");
        sb.append(
                "<p style=\"font-size: 16px; color: #555555; line-height: 1.5;\">Des alertes météorologiques sont en cours dans le département où vous avez une intervention prévue.</p>");
        sb.append(
                "<h3 style=\"color: #2c3e50; margin-top: 25px; border-bottom: 2px solid #ecf0f1; padding-bottom: 10px;\">Récapitulatif des alertes actives :</h3>");

        List<String> activeLevels = appConfig.getActiveLevels();
        List<String> activeTypes = appConfig.getActiveTypes();

        for (ContactAlerteDTO alert : alerts) {
            String levelValue = String.valueOf(alert.niveau());
            String typeValue = String.valueOf(alert.type());

            if (activeLevels.contains(levelValue) && activeTypes.contains(typeValue)) {
                String type = getAlertTypeName(typeValue);
                String advice = getAlertAdvice(typeValue);
                String level = getAlertLevelName(levelValue);
                String color = getColor(levelValue);
                String bgColor = getBgColor(levelValue);

                sb.append("<div style=\"margin-bottom: 20px; padding: 15px; border-left: 5px solid ").append(color)
                        .append("; background-color: ").append(bgColor).append("; border-radius: 0 6px 6px 0;\">");
                sb.append("<h4 style=\"margin: 0 0 8px 0; color: ").append(color).append("; font-size: 18px;\">")
                        .append(type).append(" - Niveau ").append(level).append("</h4>");
                sb.append(
                        "<p style=\"margin: 0; font-size: 15px; color: #444444; line-height: 1.5;\"><strong>Conseil : </strong>")
                        .append(advice).append("</p>");
                sb.append("</div>");
            }
        }

        sb.append(
                "<p style=\"font-size: 16px; color: #555555; margin-top: 30px; padding-top: 20px; border-top: 1px solid #ecf0f1;\">Merci de prendre les précautions nécessaires avant votre déplacement.</p>");
        sb.append("<p style=\"font-size: 16px; color: #333333; font-weight: bold;\">Merci de votre compréhension.</p>");
        sb.append("</div></div></body></html>");

        return sb.toString();
    }

    /**
     * Retourne la couleur hexadécimale associée à un niveau de vigilance pour le
     * style HTML.
     */
    private String getColor(String levelId) {
        if ("2".equals(levelId))
            return "#f39c12";
        if ("3".equals(levelId))
            return "#d35400";
        if ("4".equals(levelId))
            return "#c0392b";
        return "#27ae60";
    }

    /**
     * Retourne la couleur d'arrière-plan hexadécimale associée à un niveau de
     * vigilance pour le style HTML.
     */
    private String getBgColor(String levelId) {
        if ("2".equals(levelId))
            return "#fdf8e3";
        if ("3".equals(levelId))
            return "#fbeee6";
        if ("4".equals(levelId))
            return "#fadbd8";
        return "#eafaf1";
    }

    /**
     * Traduit le code numérique d'un type d'alerte Météo-France en libellé textuel
     * lisible.
     */
    private String getAlertTypeName(String typeId) {
        if ("1".equals(typeId))
            return "Vent Violent";
        if ("2".equals(typeId))
            return "Pluie-Inondation";
        if ("3".equals(typeId))
            return "Orages";
        if ("4".equals(typeId))
            return "Inondation";
        if ("5".equals(typeId))
            return "Neige-verglas";
        if ("6".equals(typeId))
            return "Canicule";
        if ("7".equals(typeId))
            return "Grand Froid";
        if ("8".equals(typeId))
            return "Avalanches";
        if ("9".equals(typeId))
            return "Vagues-Submersion";
        return "Phénomène météo";
    }

    /**
     * Associe un conseil de sécurité professionnel à un type d'alerte spécifique
     * pour guider les techniciens lors de leurs interventions.
     */
    private String getAlertAdvice(String typeId) {
        if ("1".equals(typeId))
            return "Sécurisez votre matériel léger en toiture et reportez les interventions en hauteur (groupes froids extérieurs, conduits d'évacuation).";
        if ("2".equals(typeId))
            return "Prudence lors des accès aux chaufferies en sous-sol (risque d'inondation) et isolez rigoureusement vos raccordements électriques.";
        if ("3".equals(typeId))
            return "Stoppez immédiatement les manipulations sur les tableaux électriques et éloignez-vous des unités climatiques extérieures.";
        if ("4".equals(typeId))
            return "N'intervenez en aucun cas dans une chaufferie ou un local technique inondé. Coupez l'alimentation générale si la sécurité le permet.";
        if ("5".equals(typeId))
            return "Anticipez vos temps de trajet et redoublez de vigilance face au risque de glissade sur les toits-terrasses et les passerelles.";
        if ("6".equals(typeId))
            return "Forte sollicitation prévue sur les systèmes de réfrigération/climatisation. Hydratez-vous et faites des pauses, surtout en comble ou en toiture.";
        if ("7".equals(typeId))
            return "Risque élevé de gel sur les tuyauteries et condensats. Prévoyez des vêtements thermiques pour les dépannages prolongés en extérieur.";
        if ("8".equals(typeId))
            return "Respectez scrupuleusement les fermetures de routes lors de vos accès aux sites ou stations d'altitude.";
        if ("9".equals(typeId))
            return "Reportez toute maintenance sur les groupes de condensation ou pompes à chaleur situés en front de mer directement exposés.";
        return "Adaptez vos Équipements de Protection Individuelle (EPI) aux conditions et restez prudent sur le site d'intervention.";
    }

    /**
     * Traduit l'identifiant numérique du niveau d'alerte en libellé textuel
     * lisible.
     */
    private String getAlertLevelName(String levelId) {
        if ("1".equals(levelId))
            return "VERT";
        if ("2".equals(levelId))
            return "JAUNE";
        if ("3".equals(levelId))
            return "ORANGE";
        if ("4".equals(levelId))
            return "ROUGE";
        return "INCONNU";
    }

    /**
     * Envoie automatiquement les e-mails d'alerte pour l'ensemble des départements
     * configurés en base de données pour la date courante.
     */
    public void sendAlertEmailsToAllDepartments() {
        String today = LocalDate.now().toString();
        List<Departement> departments = departementRepository.findAll();

        for (Departement dept : departments) {
            try {
                sendAlertEmails(today, dept.getNum());
            } catch (Exception e) {
                log.error("Error sending alert emails for department: " + dept.getNum(), e);
            }
        }
    }
}