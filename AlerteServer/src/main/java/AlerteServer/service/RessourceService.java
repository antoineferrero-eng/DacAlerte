package AlerteServer.service;

import AlerteServer.config.AppConfig;
import AlerteServer.dto.ContactAlerteDTO;
import AlerteServer.dto.RessourceManageDTO;
import AlerteServer.entity.Ressource;
import AlerteServer.exception.IdNotFoundException;
import AlerteServer.repository.RessourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RessourceService {

    private static final Logger log = LoggerFactory.getLogger(RessourceService.class);

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AppConfig appConfig;

    public List<Ressource> getAll() {
        return ressourceRepository.findAll();
    }

    public Ressource getById(String id) {
        return ressourceRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Ressource not found: " + id));
    }

    public List<ContactAlerteDTO> getContactsByAlerte(String date, String deptNum) {
        return ressourceRepository.findContactsByAlerte(LocalDate.parse(date), deptNum);
    }

    public List<RessourceManageDTO> getManageRessources(Integer level, List<String> allowedDepts) {
        if (level != null && level == 1 && allowedDepts != null && !allowedDepts.isEmpty()) {
            return ressourceRepository.findManageResourcesByDepartments(allowedDepts);
        } else {
            return ressourceRepository.findAllManageResources();
        }
    }

    public void sendManualEmail(String dkCode) {
        Ressource res = getById(dkCode);
        String to = res.getEmail();
        if (to == null || to.isEmpty() || "null".equals(to)) {
            throw new IllegalArgumentException("La ressource n'a pas d'adresse e-mail configurée");
        }

        LocalDate today = LocalDate.now();
        List<ContactAlerteDTO> alerts = ressourceRepository.findContactsByResourceAndDate(dkCode, today);

        emailService.sendManualAlertEmail(dkCode, to, alerts);
        log.info("Email d'alerte manuel envoyé avec succès à la ressource {} ({})", dkCode, to);
    }

    public void sendManualSMS(String dkCode) {
        Ressource res = getById(dkCode);
        String phone = res.getTelPortable();
        if (phone == null || phone.isEmpty() || "null".equals(phone)) {
            throw new IllegalArgumentException("La ressource n'a pas de téléphone portable configuré");
        }

        log.info("[SMS GATEWAY MOCK] Envoi de SMS d'alerte manuelle à la ressource {} au numéro : {}", dkCode, phone);
    }
}