package AlerteServer.controller;

import AlerteServer.dto.ContactAlerteDTO;
import AlerteServer.dto.RessourceDTO;
import AlerteServer.dto.RessourceManageDTO;
import AlerteServer.entity.Ressource;
import AlerteServer.service.RessourceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ressources")
public class RessourceController {
    
    private final RessourceService ressourceService;

    public RessourceController(RessourceService ressourceService) {
        this.ressourceService = ressourceService;
    }

    @GetMapping
    public List<RessourceDTO> getAll() {
        return ressourceService.getAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @GetMapping("/manage")
    public List<RessourceManageDTO> getManageRessources(
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "depts", required = false) List<String> depts) {
        return ressourceService.getManageRessources(level, depts);
    }

    @GetMapping("/{id}")
    public RessourceDTO getById(@PathVariable String id) {
        return mapToDTO(ressourceService.getById(id));
    }

    @GetMapping("/concerned")
    public List<ContactAlerteDTO> getContacts(
            @RequestParam("date") String date,
            @RequestParam("dept") String deptNum) {
        return ressourceService.getContactsByAlerte(date, deptNum);
    }

    @PostMapping("/{dkCode}/email")
    public Map<String, String> sendManualEmail(@PathVariable String dkCode) {
        ressourceService.sendManualEmail(dkCode);
        return Map.of("status", "ok", "message", "Email d'alerte manuelle envoyé");
    }

    @PostMapping("/{dkCode}/message")
    public Map<String, String> sendManualMessage(@PathVariable String dkCode) {
        ressourceService.sendManualSMS(dkCode);
        return Map.of("status", "ok", "message", "SMS d'alerte manuelle envoyé (simulé dans les logs)");
    }

    private RessourceDTO mapToDTO(Ressource res) {
        return new RessourceDTO(
                res.getDkCode(),
                res.getLibFonction(),
                res.getTelPortable(),
                res.getEmail()
        );
    }
}