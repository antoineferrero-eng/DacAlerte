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
 * Contrôleur REST exposant les endpoints de gestion et de consultation des
 * sites d'intervention.
 * Accessible publiquement sur la route `/sites`.
 */
@RestController
@RequestMapping("/sites")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @GetMapping
    public List<SiteDTO> getAll() {
        return siteService.getAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

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