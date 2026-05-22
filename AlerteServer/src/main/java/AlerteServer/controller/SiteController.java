package AlerteServer.controller;

import AlerteServer.dto.SiteDTO;
import AlerteServer.entity.Site;
import AlerteServer.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur REST exposant les endpoints de gestion et de consultation des sites d'intervention.
 * Accessible publiquement sur la route `/sites`.
 */
@RestController
@RequestMapping("/sites")
public class SiteController {

    @Autowired
    private SiteService siteService;

    /**
     * Récupère la liste de l'ensemble des sites enregistrés dans le système.
     * Appelé par le client pour afficher les emplacements disponibles.
     *
     * @return une liste de {@link SiteDTO} représentant tous les sites
     */
    @GetMapping
    public List<SiteDTO> getAll() {
        return siteService.getAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Récupère les informations détaillées d'un site à partir de son identifiant unique.
     *
     * @param id le code unique du site (souvent le code d'emplacement)
     * @return le DTO correspondant au site trouvé
     */
    @GetMapping("/{id}")
    public SiteDTO getById(@PathVariable String id) {
        return mapToDTO(siteService.getById(id));
    }

    private SiteDTO mapToDTO(Site site) {
        String parentId = (site.getParent() != null) ? site.getParent().getDkCode() : null;
        String deptNum = (site.getDepartement() != null) ? site.getDepartement().getNum() : null;
        return new SiteDTO(site.getDkCode(), deptNum, parentId);
    }
}